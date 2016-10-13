//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.model.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.InputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.InputInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.OptionalInputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.OptionalInputInterfaceTestImpl;
import uk.ac.ed.epcc.webapp.forms.TestDataProvider;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterConverter;
import uk.ac.ed.epcc.webapp.jdbc.filter.NoSQLFilterException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;


public abstract class DataObjectFactoryTestCase<D extends DataObjectFactory<O>, O extends DataObject> extends WebappTestBase implements TestDataProvider<Integer,DataObjectItemInput<O>>,
InputInterfaceTest<Integer, DataObjectItemInput<O>, DataObjectFactoryTestCase<D, O>>,
OptionalInputInterfaceTest<Integer, DataObjectItemInput<O>, DataObjectFactoryTestCase<D, O>>,
ListInputInterfaceTest
{

	
	
	public InputInterfaceTest<Integer, DataObjectItemInput<O>, DataObjectFactoryTestCase<D, O>> input_test = new InputInterfaceTestImpl<Integer, DataObjectItemInput<O>, DataObjectFactoryTestCase<D,O>>(this);
	

	public OptionalInputInterfaceTest<Integer, DataObjectItemInput<O>, DataObjectFactoryTestCase<D, O>> optional_input_test = new OptionalInputInterfaceTestImpl<Integer, DataObjectItemInput<O>, DataObjectFactoryTestCase<D,O>>(this);
	

	public ListInputInterfaceTest list_test = new ListInputInterfaceTestImpl(this);
	
   public abstract D getFactory();
	
  

   @Test
   public void testConstruct() throws SecurityException, NoSuchMethodException{
	   // DataObjectFactories implement Contexed so should have a AppContext Constructor
	   Class<? extends DataObjectFactory> clazz = getFactory().getClass();
	   boolean appcontext_const=false;
	   boolean appcontext_string_const=false;
	   try{
		   clazz.getConstructor( AppContext.class );
		   appcontext_const=true;
	   }catch( NoSuchMethodException e){
		   
	   }
	   try{
		   clazz.getConstructor( AppContext.class , String.class);
		   appcontext_string_const=true;
	   }catch( NoSuchMethodException e){
		   
	   }
	   assertTrue(appcontext_const || appcontext_string_const);
   }
   
//   public void testObjectConstruct() throws DataFault {
//	   // DataObjectFactories implement Contexed so should have a AppContext Constructor
//	   D f = getFactory();
//	   Iterator<O> it = f.getAllIterator();
//	   if( it.hasNext()){
//		    O dat = it.next();
//		    Class<? extends DataObject> c = dat.getClass();
//		    O dup = (O) ctx.makeObject(c,dat.getID());
//		    assertTrue(dup.equals(dat));
//	   }
//   }
   
   @Test
   public void testGetAllIterator() throws DataFault{
	   D f = getFactory();
	   if(f.isValid()){
	   int count=0;
	   for(Iterator<O> it = f.getAllIterator(); it.hasNext() && count < 1024; count++){
		   O o =  it.next();
		   assertTrue(f.isMine(o));
		   o.release();
	   }
	   }
   }
   
   @Test
   public void testGetCount() throws DataException{
	   D f = getFactory();
	   if( f.isValid()){
	   long expect = f.getCount(null);
	   
	   
	   assertEquals(f.exists(new SQLAndFilter<O>(f.getTarget())), expect>0);
	   
	   if( expect > 100000){
		   System.out.println("Count to large to test");
		   return;
	   }
	   long count=0;
	   for(Iterator<O> it = f.getAllIterator(); it.hasNext();){
		   O o =  it.next();
		   assert(count<expect);
		   count++;
		   o.release();
	   }
	   
	   assertEquals(count, expect);
	   System.out.println("Total is "+count);
	   }
   }
   
   @Test
   public void testFilterSet() throws DataFault{
	   DataObjectFactory<O> f = getFactory();
	   if( f.isValid()){
	   FilterResult<O> set = f.new FilterSet(null);
	   Iterator<O> it2= f.getAllIterator();
	   int i=0;
	   for(O o: set){
		   assertTrue(it2.hasNext());
		   assertTrue(f.isMine(o));
		   O o2 = it2.next();
		   assertEquals(o2, o);
		   o2.release();
		   o.release();
		   if( i++ > 100000){
			   return;
		   }
	   }
	   assertFalse(it2.hasNext());
	   }
   }
   
   @Test
   public void testLimitFilter() throws DataFault{
	   DataObjectFactory<O> f = getFactory();
	   if( f.isValid()){
	   int count=0;
	   for(Iterator<O> it = f.getAllIterator(); it.hasNext() && count < 50 ;){
		   O o =  it.next();
		   assertTrue(f.isMine(o));
		   count++;
		   o.release();
	   }

	   int lcount=0;
	   for(Iterator<O> it = f.new FilterIterator(null,0,5);it.hasNext();){
		   O o =  it.next();
		   assertTrue(f.isMine(o));
		   lcount++;
		   o.release();
	   }
	   if( count > 5){
		   assertEquals(5,lcount);
	   }else{
		   assertEquals(lcount, count);
	   }
	   int lpos=0;
	   for(Iterator<O> it = f.getAllIterator();it.hasNext() && lpos < 100 ;){
		   lcount=0;
		   for(Iterator<O> it2 = f.new FilterIterator(null,lpos,7);it2.hasNext();){
			   assertTrue(it.hasNext());
			   O o =  it.next();
			   O o2 =  it2.next();
			   assertTrue(f.isMine(o));
			   assertTrue(f.isMine(o2));
			   assertTrue(o.equals(o2));
			   lcount++;
			   lpos++;
			   o.release();
			   o2.release();
		   }
		   if( it.hasNext()){
			   assertEquals(7, lcount);
		   }
	   }
	   }
	   
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testBuildForm() throws DataFault, FieldException{
	   DataObjectFactory<O> f = getFactory();
	   if( f.isValid()){
	   for(Iterator<O> it = f.new FilterIterator(null,0,64); it.hasNext();){
		   O o =  it.next();
		   //System.out.println(o.getIdentifier());
		   Form form = new HTMLForm(f.getContext());
		   DataObjectFormFactory.buildForm(f.getContext(),f.res,form,f.getSupress(),f.getOptional(),f.getSelectors(),f.getTranslations());
		   Map h = o.getMap();
		   form.setContents(h);
		   form.validate();
		   Map m = form.getContents();
		 
		   for(Iterator it2 = m.keySet().iterator();it2.hasNext();){
			   String key = (String) it2.next();
			   Input i = form.getInput(key);
			   //System.out.println(key);
			   Object v = h.get(key);
			Object dat_value = i.convert(v);
			   Object form_value = m.get(key);
			   // map to strings as some inputs handle null in a special fashion
			   String expect = i.getString(dat_value);
			String string = i.getString(form_value);
			if( ! string.equals(expect)){
				assertEquals(expect, string);
			}
			   if( dat_value != null ){
				   assertEquals(dat_value, form_value);
			   }
		   }
		   o.release();
		   m.clear();
		   h.clear();
		   //form.clear();
		  // assertTrue(o.getHashtable().equals(form.getContents()));
	   }
	   }
   }
   
   @Test
   public void testMakeReference() throws DataFault{
	   DataObjectFactory<O> f = getFactory();
	   if( f.isValid()){
		   
		   O uncle = f.makeBDO();
	   for(Iterator<O> it = f.new FilterIterator(null,0,64); it.hasNext();){
		   O o =  it.next();
		   assertTrue( f.getTarget().isAssignableFrom(o.getClass()));
		   
		   IndexedReference<O> ref = f.makeReference(o);
		   System.out.println(ref.toString());
		   assertTrue(f.isMyReference(ref));
		   assertEquals(ref.getID(), o.getID());
		   
		   O new_o = ref.getIndexed(ctx);
		   
		   assertTrue( f.isMine(new_o));
		   assertTrue( o.equals(new_o));
		   assertFalse( o.equals(uncle));
	   }
   }
   }
   public Set<Integer> getBadData() throws Exception{
		Set<Integer> bad = new HashSet<Integer>();
		bad.add(-12);
		bad.add(-14);
		return bad;
	}

	public Set<Integer> getGoodData()  throws Exception{
		Set<Integer> good = new HashSet<Integer>();
	       DataObjectFactory<O> fac = getFactory();
	       SQLFilter<O> fil;
	       try{
	    	   fil = FilterConverter.convert(fac.getSelectFilter());
	       }catch(NoSQLFilterException e){
	    	   // All records
	    	   fil = new SQLAndFilter<O>(getFactory().getTarget());
	       }
			for(O item : fac.new FilterSet(fil,0,100)){
				good.add(item.getID());
			}
		return good;
	}

	public DataObjectItemInput<O> getInput() {
		return getFactory().getInput();
	}
	
	
	@Test
	public void testConvert() throws Exception{
		DataObjectFactory<O> fac = getFactory();
		DataObjectItemInput<O> input = fac.getInput();
		for( Integer i : getGoodData()){
			O item = fac.find(i.intValue());
			assertEquals(i, input.convert(item));
			IndexedReference<O> ref = fac.makeReference(item);
			assertEquals(i, input.convert(ref));
		}
	}
	
	@Override
	@Test
	public final void testIsOptional() throws Exception {
		optional_input_test.testIsOptional();
	}


	@Override
	@Test
	public final void testGetKey() throws Exception {
		input_test.testGetKey();
		
	}


	@Override
	@Test
	public final void testMakeHtml() throws Exception {
		input_test.testMakeHtml();
	}


	@Override
	@Test
	public final void testMakeSwing() throws Exception {
		input_test.testMakeSwing();
		
	}


	@Override
	@Test
	public final void testGood() throws TypeError, Exception {
		input_test.testGood();
		
	}


	@Override
	@Test
	public final void testBad() throws Exception {
		input_test.testBad();
	}


	@Override
	@Test
	public final void testGetString() throws Exception {
		input_test.testGetString();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetItembyValue()
	 */
	@Override
	@Test
	public final void testGetItembyValue() throws Exception {
		list_test.testGetItembyValue();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetItems()
	 */
	@Override
	@Test
	public final void testGetItems() throws Exception {
		list_test.testGetItems();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetTagByItem()
	 */
	@Override
	@Test
	public final void testGetTagByItem() throws Exception {
		list_test.testGetTagByItem();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetTagByValue()
	 */
	@Override
	@Test
	public final void testGetTagByValue() throws Exception {
		list_test.testGetTagByValue();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ListInputInterfaceTest#testGetText()
	 */
	@Override
	@Test
	public final void testGetText() throws Exception {
		list_test.testGetText();
		
	}

}