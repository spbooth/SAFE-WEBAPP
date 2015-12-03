//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.apps;


import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.registry.FormFactoryProvider;
import uk.ac.ed.epcc.webapp.forms.registry.FormFactoryProviderRegistry;
import uk.ac.ed.epcc.webapp.forms.registry.FormFactoryProviderTransitionProvider;
import uk.ac.ed.epcc.webapp.forms.registry.FormOperations;
import uk.ac.ed.epcc.webapp.session.SessionService;

public class AdminForms extends GraphicsCommand implements Command {
   
	public AdminForms(AppContext conn) {
		super(conn);
	}

	@SuppressWarnings("unchecked")
	protected JPanel getMainPanel(JFrame frame,SessionService session_service) {
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		main.add(new JLabel("Admin Forms"));
		main.setName("main");
		for(String name : getContext().getInitParameter("form.registry.list","").split(",") ){
			
			if( name.trim().length() > 0 ){
				int count=0;
				FormFactoryProviderRegistry registry = getContext().makeObjectWithDefault(FormFactoryProviderRegistry.class,null,name.trim());
				if(registry != null ){
					JPanel section = new JPanel();
					section.setLayout(new BoxLayout(section, BoxLayout.PAGE_AXIS));
					
					//JLabel section_title = new JLabel(registry.getTitle());
					
					////section_title.setBackground(Color.DARK_GRAY);
					////section_title.setOpaque(true); // transparent by default
					//section.add(section_title);
					section.setBorder(BorderFactory.createTitledBorder(registry.getTitle()));
					section.setName(registry.getTitle());

					for(Iterator it=registry.getTypes(); it.hasNext(); ){
						FormFactoryProvider t= (FormFactoryProvider) it.next();
						
						if( t.canCreate(session_service)|| t.canUpdate(session_service)){
							count ++;
							JPanel type = new JPanel();

							section.add(type);
							type.add(new JLabel(t.getName()));

							FormFactoryProviderTransitionProvider tp = new FormFactoryProviderTransitionProvider(getContext(), t.getName(), t);


							if( t.canCreate(session_service) ){
								JButton button = new JButton("Create new "+t.getName());
								type.add(button);
								button.addActionListener( new TransitionActionListener(tp, FormOperations.Create, null));
							}else{
								log.debug("Cannot create "+t.getName());
							}
							if( t.canUpdate(session_service) ){
								JButton button = new JButton("Update "+t.getName());
								type.add(button);
								button.addActionListener( new TransitionActionListener(tp, FormOperations.Update, null));
							}else{
								log.debug("cannot update "+t.getName());
							}
						}
					}
					if( count > 0){
						main.add(section);
					}
				}
			}
		}
		return main;
	}

	public String description() {
		return "Admin GUI forms";
	}

	public String help() {
		return "Accesses admin forms through stand alone gui";
	}
	

}