package uk.ac.ed.epcc.webapp.ssl;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.config.ConfigServiceListener;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.resource.ResourceService;
/** Service for creating {@link SSLContext} objects configured from
 * the AppContext
 * 
 * Once created the 
 * 
 * @author spb
 *
 */
@PreRequisiteService({ConfigService.class ,ResourceService.class})
public class SSLService implements Contexed, ConfigServiceListener, AppContextService<SSLService> {

	public static final Feature INSTALL_DEFAULT_FEATURE = new Feature("sslservice.install_default_context", true, "When the SSL service makes its default SSLContext this is also installed as the general default");
	public static final String DEFAULT = "default";
	private static SSLContext default_context=null;
	private ConfigService config_serv;
	private AppContext conn;
	public SSLService(AppContext conn){
		this.conn=conn;
		config_serv = conn.getService(ConfigService.class);
		config_serv.addListener(this);
	}
	public void cleanup() {
		
		
	}

	
	/** get a SSLContext configured by a string tag.
	 * 
	 * @param protocol Type of SSLContext
	 * @param tag config tag
	 * @return SSLContext
	 * @throws Exception 
	 */
	public SSLContext getSSLContext(String protocol,String tag) throws Exception{
		SSLContext ctx = SSLContext.getInstance(protocol);
		ctx.init(getKeyManagers(tag), getTrustManagers(tag), null);
		return ctx;
	}
	
	/** Method to get the Default SSLContext.
	 * Looks for a context configured by the string
	 * default value or a cached result from a previous call.
	 * 
	 * 
	 * @return SSLContext
	 * @throws Exception 
	 */
	public SSLContext makeDefaultContext() throws Exception{
		if( default_context == null ){
			default_context = getSSLContext("SSL",DEFAULT);
			

			if( default_context != null ){
				SSLContext.setDefault(default_context);
			}else{
					default_context = SSLContext.getDefault();
			}
		}
		return default_context;
	}
	/** Get the statically cached {@link SSLContext}
	 * This needs to have been set by a call to {@link #makeDefaultContext()}
	 * 
	 * @return SSLContext
	 */
	public static SSLContext getCachedDefaultContext(){
		return default_context;
	}
	public final Class<? super SSLService> getType() {
		return SSLService.class;
	}
	public void resetConfig() {
		default_context=null;
	}
	
 
    
 
    private KeyManager[] getKeyManagers(String tag)
    throws Exception
    {
 
    	ResourceService resources=conn.getService(ResourceService.class);
        //Init a key store with the given file.
 
        String alg=KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory kmFact=KeyManagerFactory.getInstance(alg);
 
        String keystore = conn.getInitParameter("sslservice."+tag+".keystore.location");
        String keypass = conn.getInitParameter("sslservice."+tag+".keystore.pass","");
        String keytype = conn.getInitParameter("sslservice."+tag+".keystore.type");
        if( keystore == null || keypass == null || keytype == null ){
        	return kmFact.getKeyManagers(); 
        }
        InputStream fis = resources.getResourceAsStream(keystore);
        if( fis == null ){
        	throw new ConsistencyError("No resoruce at "+keystore);
        }
        KeyStore ks=KeyStore.getInstance(keytype);
        ks.load(fis, keypass.toCharArray());
       
        fis.close();
 
        //Init the key manager factory with the loaded key store
        kmFact.init(ks,  keypass.toCharArray());
 
        KeyManager[] kms=kmFact.getKeyManagers();
        return kms;
    }
 
 protected TrustManager[] getTrustManagers(String tag) throws Exception
    {
	 String truststore = conn.getInitParameter("sslservice."+tag+".truststore.location");
     String trustpass = conn.getInitParameter("sslservice."+tag+".truststore.pass","");
     String trusttype = conn.getInitParameter("sslservice."+tag+".truststore.type");
   
        String alg=TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmFact=TrustManagerFactory.getInstance(alg);
        ResourceService resources=conn.getService(ResourceService.class);

        if( truststore==null || trusttype==null){
        	return tmFact.getTrustManagers();
        }
        InputStream fis = resources.getResourceAsStream(truststore);
        if( fis == null ){
        	throw new ConsistencyError("No resoruce at "+truststore);
        }
        KeyStore ks=KeyStore.getInstance(trusttype);
        ks.load(fis, trustpass.toCharArray());	        
        fis.close();
 
        tmFact.init(ks);
 
        TrustManager[] tms=tmFact.getTrustManagers();
        return tms;
    }
public AppContext getContext() {
	return conn;
}
}
