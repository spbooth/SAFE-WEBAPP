package uk.ac.ed.epcc.webapp.email;

import java.io.*;
import java.util.*;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.content.*;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.result.ServeDataResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.AnonymisingFactory;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory.Lock;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterDelete;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLIdFilter;
import uk.ac.ed.epcc.webapp.model.data.stream.*;
import uk.ac.ed.epcc.webapp.model.mail.MessageDataObject;
import uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Table to hold queued email messages. These are usually messages that failed to send originally
 * and need to be retried. It is also possible to configure all messages to be queued to the database first
 * to be extracted and sent by a separate process. This might be needed if the normal servers don't have direct access to a mail
 * relay.
 * 
 * @param <Q>
 */
public class QueuedMessages<Q extends QueuedMessages.QueuedMessage> extends DataObjectFactory<Q> implements AnonymisingFactory, ServeDataProducer{
	private static final String QUEUED_EMAILS_RETRY_LOCK = "queued_emails.retry_lock";
	public static final String RETRY = "Retry";
	public static final String LAST_RETRY = "LastRetry";
	
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table) {
		TableSpecification spec = MessageDataObject.getTableSpecification();
		spec.setField(RETRY, new IntegerFieldType());
		spec.setField(LAST_RETRY, new DateFieldType(true, null));
		return spec;
	}

	public static final String DEFAULT_TABLE="QueuedMessages";
	
	public static QueuedMessages getFactory(AppContext conn) {
		return conn.makeObject(QueuedMessages.class, DEFAULT_TABLE);
	}
	
	public QueuedMessages(AppContext conn,String table) {
		setContext(conn, table);
	}
	
	public class QueuedMessage extends MessageDataObject implements UIGenerator {

		protected QueuedMessage(Record r) {
			super(r);
		}
		
		public Date getSentDate() {
			try {
				return getMessage().getSentDate();
			} catch (Exception e) {
				getLogger().error("Error getting Sent date", e);
			}
			return null;
		}
		public int getRetry() {
			return record.getIntProperty(RETRY,0);
		}
		public Date getLastRetry() {
			return record.getDateProperty(LAST_RETRY);
		}
		
		public void recordRetry() throws DataFault {
			record.setProperty(RETRY, getRetry()+1);
			record.setProperty(LAST_RETRY, getContext().getService(CurrentTimeService.class).getCurrentTime());
			commit();
		}

		@Override
		protected String[] ignoreList() {
			// Don't save message id as it will be re-written on attempted send
			return new String[] {"Message-ID"};
		}
		public MimeStreamData getData() throws DataFault, IOException, MessagingException {
			ByteArrayMimeStreamData result = new ByteArrayMimeStreamData();
			getMessage().writeTo(result.getOutputStream());
			result.setName(getDownloadName());
			result.setMimeType("message/rfc822");
			return result;
		}

		private String getDownloadName() {
			return Integer.toString(getID())+".msg";
		}

		@Override
		public ContentBuilder addContent(ContentBuilder builder) {
			LinkedList<String> args = new LinkedList<>();
			args.add(Integer.toString(getID()));
			builder.addLink(getContext(), getIdentifier(), new ServeDataResult(QueuedMessages.this, args));
			return builder;
		}
		
		public String getExportString() throws DataFault, IOException, MessagingException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			getMessage().writeTo(out, ignoreList());
			Base64.Encoder enc = Base64.getEncoder();
			return enc.encodeToString(out.toByteArray());
		}
		
		
		
	}

	@Override
	protected Q makeBDO(Record res) throws DataFault {
		return (Q) new QueuedMessage(res);
	}

	/** Add a {@link MimeMessage} to the queue.
	 * 
	 * @param m
	 * @return
	 * @throws DataFault
	 */
	public Q queueMessage(MimeMessage m) throws DataFault {
	
		Q qm = makeBDO();
		qm.setMessage(m);
		qm.commit();
		return qm;
	}

	@Override
	public void anonymise() throws DataFault {
		// Just delete all records.
		FilterDelete del = new FilterDelete<>(res);
		del.delete(null);
	}
	public Table getMessageQueue() throws DataFault {
		Table t = new Table();
		for(QueuedMessage m : all()) {
			t.put("Subject", m, m.getSubject());
			t.put("Recipients", m, m.getRecipients());
			t.put("Sent", m, m.getSentDate());
			t.put("RetryCount", m, m.getRetry());
			t.put("LastRetry", m, m.getLastRetry());
			t.put("Download", m, new Button(getContext(), "download", new ServeDataResult(this, Integer.toString(m.getID()))));
		}
		return t;
	}
	public long getQueuedMessageCount() {
		try {
			return getCount(null);
		} catch (DataException e) {
			getLogger().error("Error counting queued messages",e);
			return 0L;
		}
	}
	public void delete(int id) throws DataFault {
		FilterDelete<Q> del = new FilterDelete<>(res);
		del.delete(new SQLIdFilter<>(res, id));
	}
	
	public int retry() {
		if( Emailer.EMAIL_FORCE_QUEUE_FEATURE.isEnabled(getContext())) {
			return 0;
		}
		try {
			// Shortcut check. Don't take the lock unless there seem to be some
			// records to process.
			if( ! exists(null)) {
				return 0;
			}
		} catch (DataException e) {
			getLogger().error("Error checking for any queued emails",e);
			return 0;
		}
		Emailer em = Emailer.getFactory(getContext());
		LockFactory locks = LockFactory.getFactory(getContext());
		int count=0;
		try(Lock lock = locks.makeFromString(QUEUED_EMAILS_RETRY_LOCK)){
			if( lock.takeLock()) {
				// Get list of ids as we will be doing deletes
				LinkedHashSet<Integer> ids = new LinkedHashSet<>();
				for(Q m : all()) {
					ids.add(m.getID());
				}
				for(Integer id : ids) {
					Q m = find(id);
					if(em.retry(m)) {
						count++;
					}
				}
			}
		} catch (Exception e) {
			getLogger().error("Error retrying queued emails", e);
		}
		return count;
	}
	/** Copy all pending emails to a {@link StreamData} and remove from queue
	 * 
	 * @return
	 * @throws DataFault
	 * @throws ParseException
	 * @throws Exception
	 */
	public StreamData exportMessages() throws DataFault, ParseException, Exception {
		ByteArrayStreamData data = new ByteArrayStreamData();
		LockFactory locks = LockFactory.getFactory(getContext());
		
		try(Lock lock = locks.makeFromString(QUEUED_EMAILS_RETRY_LOCK)){
			OutputStream stream = data.getOutputStream();
			Writer w = new OutputStreamWriter(stream);
			Set<Integer> deletes = new HashSet<>();
			if( lock.takeLock()) {
				for(Q m : all()) {
					try {
						w.write(m.getExportString());
						w.write('\n');
						deletes.add(m.getID());
					}catch(Exception e) {
						getLogger().error("Error exporting queued message", e);
					}
				}
				w.close();
				// only delete when all processed sucessfully
				for(Integer id : deletes) {
					Q m = find(id);
					m.delete();
				}
			}
		}
		return data;
	}
	
	public void importMessages(StreamData data) throws IOException, MessagingException, DataFault {
		InputStream is = data.getInputStream();
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		Session session = Session.getInstance(getContext().getProperties(),
				null);
		Base64.Decoder dec = Base64.getDecoder();
		for(String line=r.readLine(); line !=null ; line=r.readLine()) {
			ByteArrayInputStream ms = new ByteArrayInputStream(dec.decode(line));
			MimeMessage m = new MimeMessage(session, ms);
			queueMessage(m);
		}	
	}

	@Override
	public MimeStreamData getData(SessionService user, List<String> path) throws Exception {
		QueuedMessage m = find(Integer.parseInt(path.get(0)));
		if( user != null && user.hasRelationship(this, m, "download")) {
			return m.getData();
		}
		return null;
	}

	@Override
	public String getDownloadName(SessionService user, List<String> path) throws Exception {
		QueuedMessage m = find(Integer.parseInt(path.get(0)));
		return m.getDownloadName();
	}
}
