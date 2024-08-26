package il.cshaifasweng.OCSFMediatorExample.server;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.ZoneId;

import java.util.*;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;

import il.cshaifasweng.OCSFMediatorExample.server.EmailSender;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

import java.io.Serializable;
import java.security.PrivateKey;
import java.time.LocalDateTime;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private static SessionFactory sessionFactory = getSessionFactory(SimpleChatServer.password);
	public static final String[] BRANCHES = {"Sakhnin", "Haifa", "Nazareth", "Nhif"};
	// this variable for handle the link
	private HttpServer httpServer;
	private static final AtomicInteger linkCounter = new AtomicInteger(1);

	private Message reports_message = new Message(0, "reports_message");
	private static Map<Integer, Map<Integer, Map<Integer, Integer>>> multiEntryTicketSales = new HashMap<>();
	static {
		if (multiEntryTicketSales.isEmpty()) {
			int[] years = {2024, 2023, 2022};
			for (int year : years) {
				multiEntryTicketSales.put(year, new HashMap<>());
				for (int month = 1; month <= 12; month++) {
					multiEntryTicketSales.get(year).put(month, new HashMap<>());
					int daysInMonth = getDaysInMonth(month, year);
					for (int day = 1; day <= daysInMonth; day++) {
						multiEntryTicketSales.get(year).get(month).put(day, 0);
					}
				}
			}
		}
	}


	public static SessionFactory getSessionFactory(String Password) throws
			HibernateException {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.connection.password",Password);

		// Add ALL of your entities here. You can also try adding a whole package.
		configuration.addAnnotatedClass(Movie.class);
		configuration.addAnnotatedClass(Screening.class);
		configuration.addAnnotatedClass(Complains.class);
		configuration.addAnnotatedClass(EditedDetails.class);
		configuration.addAnnotatedClass(IdUser.class);
		configuration.addAnnotatedClass(MultiEntryTicket.class);
		configuration.addAnnotatedClass(Reports.class);
		configuration.addAnnotatedClass(UserPurchases.class);
		configuration.addAnnotatedClass(Worker.class);

		ServiceRegistry serviceRegistry = new
				StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties())
				.build();
		SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		return sessionFactory;
	}

	public SimpleServer(int port) {
		super(port);
		try {
			initHttpServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initHttpServer() throws IOException {
		httpServer = HttpServer.create(new InetSocketAddress(8080), 0);

//		// Add contexts for different movies
//		httpServer.createContext("/movie1", new MovieLink("https://chatgpt.com/", LocalTime.of(9, 0), LocalTime.of(12, 0)));
//		httpServer.createContext("/movie2", new MovieLink("https://chatgpt.com/", LocalTime.of(13, 0), LocalTime.of(16, 0)));
//		httpServer.createContext("/movie3", new MovieLink("https://example.com/movie3", LocalTime.of(17, 0), LocalTime.of(20, 0)));

		httpServer.setExecutor(null); // Use default executor
		httpServer.start();
		System.out.println("HTTP server is running on http://"+SimpleChatServer.host+":8080/");
	}
	/////////////////////////////////////////////// HELPER FUNCTIONS ////////////////////////////////////////////////////////////////////


	private static List<Movie> get_near_movies()throws Exception
	{
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Movie> cq = cb.createQuery(Movie.class);
		Root<Movie> movie = cq.from(Movie.class);
		Join<Movie, Screening> screeningTime = movie.join("screenings");

		Date now = new Date();
		Date nextWeek = Date.from(Instant.now().plus(7, ChronoUnit.DAYS));

		cq.select(movie).distinct(true)
				.where(cb.between(screeningTime.get("date_time"), now, nextWeek));

		List<Movie> result =  session.createQuery(cq).getResultList();
		session.getTransaction().commit();
		session.close();
		return result;
	}

	private static List<Movie> getAllMovies() throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
		query.from(Movie.class);
		List<Movie> data = session.createQuery(query).getResultList();
		session.getTransaction().commit();
		session.close();
		return data;
	}
	private void remove_movie (Movie movie) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.delete(movie);
		session.getTransaction().commit();
		session.close();

	}
	private void insert_movie (Movie movie) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(movie);
		session.getTransaction().commit();
		session.close();
	}
	private void update_movie (Movie movie) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(movie);
		session.getTransaction().commit();
		session.close();

	}
	private List<Movie> get_movies_by_name(String name) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
		Root<Movie> root =  query.from(Movie.class);
		Predicate makePredicate = builder.like(root.get("movie_name"), "%"+name+"%");
		query.select(root).where(makePredicate);
		List<Movie> data = session.createQuery(query).getResultList();
		session.getTransaction().commit();
		session.close();
		return data;
	}
	private void add_new_screening(Screening screening) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(screening);
		session.getTransaction().commit();
		session.close();
	}
	private List<Screening> get_screening_for_movie(Movie movie)
	{
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Screening> query = builder.createQuery(Screening.class);
		Root<Screening> root =  query.from(Screening.class);
		query.select(root).where(builder.equal(root.get("movie"), movie));
		List<Screening> data = session.createQuery(query).getResultList();
		session.getTransaction().commit();
		session.close();
		return data;
	}
	private Screening get_screening(int screening_id)
	{
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Screening> query = builder.createQuery(Screening.class);
		Root<Screening> root =  query.from(Screening.class);
		query.select(root).where(builder.equal(root.get("auto_number_screening"), screening_id));
		Screening data = session.createQuery(query).uniqueResult();
		session.getTransaction().commit();
		session.close();
		return data;


	}
	private void remove_screening(Screening screening) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.delete(screening);
		session.getTransaction().commit();
		session.close();
	}
	private List<Screening> search_sreening_branch_and_movie(String branch,Movie movie) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Screening> query = builder.createQuery(Screening.class);
		Root<Screening> root =  query.from(Screening.class);
		Predicate predicate_branch =  builder.like(root.get("branch"), "%"+branch+"%");
		Predicate predicate_movie =  builder.equal(root.get("movie"), movie);
		query.select(root).where(builder.and(predicate_branch, predicate_movie));

		List<Screening> data = session.createQuery(query).getResultList();
		session.getTransaction().commit();
		session.close();
		return data;
	}
	private void update_screening(Screening screening)
	{
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(screening);
		session.getTransaction().commit();
		session.close();


	}
	private void update_all_prices(int new_price) throws Exception
	{
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		List<Movie> movies = getAllMovies();
		for (Movie movie : movies)
		{
			movie.setPrice(new_price);
			session.update(movie);
		}
		session.getTransaction().commit();
		session.close();

	}
	private static Date Add_2dates(Date date,Date time)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		// Get hours, minutes, and seconds from the time
		Calendar timeCalendar = Calendar.getInstance();
		timeCalendar.setTime(time);

		int hoursToAdd = timeCalendar.get(Calendar.HOUR_OF_DAY);
		int minutesToAdd = timeCalendar.get(Calendar.MINUTE);
		int secondsToAdd = timeCalendar.get(Calendar.SECOND);

		// Add time to the date
		calendar.add(Calendar.HOUR_OF_DAY, hoursToAdd);
		calendar.add(Calendar.MINUTE, minutesToAdd);
		calendar.add(Calendar.SECOND, secondsToAdd);

		// Get the updated date
		Date updatedDate = calendar.getTime();
		return updatedDate;
	}
	private boolean check_the_new_screening(Screening screening,boolean is_update) throws Exception
	{
		Screening intersection1 = null;
		Date begin_time = screening.getDate_time();
		Date end_time =  Add_2dates(begin_time,screening.getMovie().getTime_());
		List<Movie> movies = getAllMovies();
		for (Movie movie : movies)
		{
			List<Screening> screenings = get_screening_for_movie(movie);
			for(Screening current_screening : screenings)
			{
				if (!current_screening.getBranch().equals( screening.getBranch())||current_screening.getRoom_number()!=screening.getRoom_number())
					continue;
				Date current_begin = current_screening.getDate_time();
				Date current_end =  Add_2dates(current_begin,current_screening.getMovie().getTime_());
				if(!(begin_time.after(current_end) || end_time.before(current_begin)))
				{
					if(intersection1==null)
					{
						intersection1 = current_screening;
					}
					else
					{
						return false;
					}
				}
			}
		}
		if(intersection1 == null)
		{
			return true;
		}
		if(is_update)
		{
			if(intersection1.getAuto_number_screening() == screening.getAuto_number_screening())
			{
				return true;
			}
			else {
				return false;
			}
		}
		else
		{
			return false;
		}

	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List<UserPurchases> delete_user_purchases(int auto_num,String id, Message message) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		// Find the UserPurchases object with the specified auto_num

		UserPurchases purchase = session.get(UserPurchases.class, auto_num);

		// If the purchase is not found, return false
		if (purchase == null) {
			session.getTransaction().rollback();
			session.close();
			return  search_user_purchases(id);
		}

		reports_message.setObject(purchase);
		reports_message.setObject2(message.getObject3());
		reports_message.setMessage("cancelPurchase");
		update_reports();

		// Delete the UserPurchases object
		session.delete(purchase);

		// Commit the transaction
		session.getTransaction().commit();
		session.close();
		List<UserPurchases> data = search_user_purchases(id);
		return data;
	}


	private List<UserPurchases> search_user_purchases(String id) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<UserPurchases> query = builder.createQuery(UserPurchases.class);
		Root<UserPurchases> root= query.from(UserPurchases.class);
		String queryString1 = "SELECT u FROM IdUser u WHERE u.user_id = :user_id";
		Query<IdUser> query1 = session.createQuery(queryString1, IdUser.class);
		query1.setParameter("user_id", id);
		IdUser user = query1.uniqueResult();
		query.select(root).where(builder.equal(root.get("id_user"), user));
		List<UserPurchases> data = session.createQuery(query).getResultList();
		session.getTransaction().commit();
		session.close();

		return data;
	}

	private int get_remain_tickets(String id) {

		Session session = sessionFactory.openSession();
		session.beginTransaction();

		// Retrieve the user by user ID
		String queryString1 = "SELECT u FROM IdUser u WHERE u.user_id = :user_id";
		Query<IdUser> query1 = session.createQuery(queryString1, IdUser.class);
		query1.setParameter("user_id", id);
		IdUser user = query1.uniqueResult();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<MultiEntryTicket> query = builder.createQuery(MultiEntryTicket.class);
		Root<MultiEntryTicket> root = query.from(MultiEntryTicket.class);
		query.select(root).where(builder.equal(root.get("id_user"), user));

		MultiEntryTicket ticket = session.createQuery(query).uniqueResult();

		session.getTransaction().commit();
		session.close();

		if (ticket != null) {
			return ticket.getRemain_tickets();
		}
		return 0;
	}


	private List<Movie> search_with_filter(Movie movie,int year2 , String sorting_attribute,String Sorting_direction) throws Exception
	{
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<Movie> query = builder.createQuery(Movie.class);
		Root<Movie> root =  query.from(Movie.class);
		Predicate namePredicate = builder.like(root.get("movie_name"), "%"+movie.getMovie_name()+"%");
		Predicate yearPredicate = builder.between(root.get("year_"),movie.getYear_(),year2);
		Predicate ratingPredicate = builder.greaterThanOrEqualTo(root.get("rating"), movie.getRating());
		Predicate categoryPredicate = builder.equal(root.get("category"), movie.getCategory());
		Predicate directorPredicate = builder.like(root.get("director"), "%"+movie.getDirector()+"%");
		Predicate mainActorPredicat = builder.like(root.get("main_actors"),"%"+movie.getMain_actors()+"%");
		if(movie.getCategory()!=null && !movie.getCategory().isEmpty())
			query.select(root).where(namePredicate,yearPredicate,ratingPredicate,categoryPredicate ,directorPredicate,mainActorPredicat);
		else
			query.select(root).where(namePredicate,yearPredicate,ratingPredicate ,directorPredicate,mainActorPredicat);
		if (Sorting_direction.equals("asc"))
		{
			query.orderBy(builder.asc(root.get(sorting_attribute)));
		}
		else if (Sorting_direction.equals("desc"))
		{
			query.orderBy(builder.desc(root.get(sorting_attribute)));
		}
		List<Movie> data = session.createQuery(query).getResultList();
		session.getTransaction().commit();
		session.close();
		return data;
	}

	private void SignOut_IDUser(IdUser user)
	{
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		user.setIsLoggedIn(false);
		session.update(user);
		session.getTransaction().commit();
		session.close();
	}

	private void SignOut_Worker(Worker worker)
	{
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		worker.setIs_worker_loggedIn(false);
		session.update(worker);
		session.getTransaction().commit();
		session.close();
	}


	private List<Screening> getScreeningForMovie(Movie movie){
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Screening> query = builder.createQuery(Screening.class);
		Root<Screening> root =  query.from(Screening.class);
		Predicate namePredicate = builder.equal(root.get("movie"),movie);
		query.select(root).where(namePredicate);
		List<Screening> data = session.createQuery(query).getResultList();
		session.getTransaction().commit();
		session.close();
		return data;
	}

	private IdUser getIUFromId(String id){
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<IdUser> query = builder.createQuery(IdUser.class);
		Root<IdUser> root =  query.from(IdUser.class);
		Predicate idPredicate = builder.equal(root.get("user_id"), id);
		query.select(root).where(idPredicate);
		List<IdUser> data = session.createQuery(query).getResultList();


		session.getTransaction().commit();
		session.close();

		if (data.size() != 1)
			return null;
		return data.get(0);
	}

	private UserPurchases getPurchaseByAuto(int auto_num) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<UserPurchases> query = builder.createQuery(UserPurchases.class);
		Root<UserPurchases> root = query.from(UserPurchases.class);
		Predicate namePredicate = builder.equal(root.get("auto_number_purchase"), auto_num);
		query.select(root).where(namePredicate);
		UserPurchases data = session.createQuery(query).getSingleResult();
		session.getTransaction().commit();
		session.close();
		return data;
	}


	private MultiEntryTicket getMultiTicketUsingIdUser_not_list(IdUser ID) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<MultiEntryTicket> query = builder.createQuery(MultiEntryTicket.class);
		Root<MultiEntryTicket> root = query.from(MultiEntryTicket.class);
		Predicate namePredicate = builder.equal(root.get("id_user"), ID);
		query.select(root).where(namePredicate);
		MultiEntryTicket data = session.createQuery(query).getSingleResult();
		session.getTransaction().commit();
		session.close();
		return data;
	}

	private List<MultiEntryTicket> getMultiTicketUsingIdUser(IdUser ID) {
		List<MultiEntryTicket> data = new ArrayList<>();
		System.out.println("MT func 1");

		// Try-with-resources ensures session is closed automatically.
		try (Session session = sessionFactory.openSession()) {
			session.beginTransaction();

			// Using CriteriaBuilder for constructing the query
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<MultiEntryTicket> query = builder.createQuery(MultiEntryTicket.class);
			Root<MultiEntryTicket> root = query.from(MultiEntryTicket.class);



			// Adding the predicate condition
			Predicate namePredicate = builder.equal(root.get("id_user"), ID);



			query.select(root).where(namePredicate);


			// Execute query and get the result
			data = session.createQuery(query).getResultList();


			// Flush session before commit
			session.flush();

			// Commit transaction
			session.getTransaction().commit();


			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}



	private void updateMT(MultiEntryTicket multiEntryTicket) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(multiEntryTicket);
		session.getTransaction().commit();
		session.close();
	}


	private void saveUP (UserPurchases userPurchases){
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		IdUser user1 = getOrSaveIdUser(session,userPurchases.getId_user());
		userPurchases.setId_user(user1);
		// get userId from dataBase
		// set that userId to be userPur
		// then save

		if (!session.contains(userPurchases)) {
			session.saveOrUpdate(userPurchases);  // This will either save or update depending on the state
		}
		else session.update(userPurchases);
		session.getTransaction().commit();
		session.close();
	}

	private List<Complains> search_data(boolean do_show_not_responded) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Complains> query = builder.createQuery(Complains.class);
		query.from(Complains.class);
		List<Complains> data = session.createQuery(query).getResultList();
		session.getTransaction().commit();
		session.close();
		if (do_show_not_responded) {
			data.removeIf(complain -> complain.getStatus());
		}
		else {
			data.removeIf(complain -> !complain.getStatus());
		}
		return data;
	}

	private List<Complains> update_respond(int auto_num, String respond_text, boolean phase) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		// Find the object with the specified auto_num
		Complains complains = session.get(Complains.class, auto_num);

		if (complains == null) {
			session.getTransaction().rollback();
			session.close();
			return  search_data(phase);
		}
		// update the complain object
		complains.setRespond(respond_text);
		complains.setStatus(true);
		session.update(complains);

		// Commit the transaction
		session.getTransaction().commit();
		session.close();
		List<Complains> data = search_data(phase);
		return data;
	}

	private IdUser getOrSaveIdUser( IdUser idUser) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<IdUser> idUserQuery = builder.createQuery(IdUser.class);
		Root<IdUser> idUserRoot = idUserQuery.from(IdUser.class);
		idUserQuery.select(idUserRoot).where(builder.equal(idUserRoot.get("user_id"), idUser.getUser_id()));
		IdUser existingIdUser = session.createQuery(idUserQuery).uniqueResult();

		if (existingIdUser != null) {
			existingIdUser.setEmail(idUser.getEmail());
			existingIdUser.setName(idUser.getName());
			session.update(existingIdUser);
			session.getTransaction().commit();
			session.close();


			return existingIdUser;
		} else {
			session.save(idUser);
			session.getTransaction().commit();
			session.close();
			return idUser;
		}

	}


	private void updateOrSaveMultiEntryTicket( MultiEntryTicket t, IdUser idUser) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<MultiEntryTicket> query = builder.createQuery(MultiEntryTicket.class);
		Root<MultiEntryTicket> root = query.from(MultiEntryTicket.class);
		query.select(root).where(builder.equal(root.get("id_user"), idUser));
		MultiEntryTicket existingTicket = session.createQuery(query).uniqueResult();

		if (existingTicket != null) {
			existingTicket.setRemain_tickets(existingTicket.getRemain_tickets() + t.getRemain_tickets());
			session.update(existingTicket);
		} else {
			t.setId_user(idUser);
			session.save(t);
		}
		session.getTransaction().commit();
		session.close();
	}

	private void sendEmail1(UserPurchases userPurchases){
		EmailSender emailSender = new EmailSender();
		String[] recipients = {userPurchases.getId_user().getEmail()};
		String subject = "Thank You for Your Purchase at Luna Aura";

		String name = userPurchases.getId_user().getName();
		String id = userPurchases.getId_user().getUser_id();
		LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
		String formattedDate = date.format(formatter);

		String body = "<html>"
				+ "<body style='font-family: Arial, sans-serif; color: #333;'>"
				+ "<div style='max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd;'>"
				+ "<div style='text-align: center;'>"
				+ "<img src='YOUR_LOGO_URL' alt='Luna Aura' style='width: 100px; margin-bottom: 20px;'/>"
				+ "<h1 style='font-size: 24px; color: #555;'>Thank You.</h1>"
				+ "</div>"
				+ "<p>Hi " + name + "!</p>"
				+ "<p>Thanks for your purchase from Luna Aura.</p>"
				+ "<h2 style='color: #555;'>INVOICE ID: " + id + "</h2>"
				+ "<p><em>(Please keep a copy of this receipt for your records.)</em></p>"
				+ "<hr style='border: 0; height: 1px; background-color: #ddd;'/>"
				+ "<h3 style='color: #555;'>YOUR ORDER INFORMATION:</h3>"
				+ "<p><strong>Order ID:</strong> " + userPurchases.getAuto_number_purchase() + "<br/>"
				+ "<strong>Order Date:</strong> " + formattedDate + "<br/>"
				+ "<strong>Source:</strong> Luna Aura</p>"
				+ "<p><strong>Amout:</strong> >" + userPurchases.getPayment_amount()+ "</p>"
				+ "<p><strong>Branch:</strong> >" + userPurchases.getScreening().getBranch() + "</p>"
				+ "<p><strong>Movie name:</strong> " + userPurchases.getMovie_name() + "<br/>"
				+ "<p><strong>Room number:</strong> " + userPurchases.getScreening().getRoom_number() + "<br/>"
				+ "<p><strong>Screening Time:</strong> " + userPurchases.getScreening_time() + "<br/>"
				+ "<p><strong>Your seats:</strong> " + userPurchases.getSeats() + "<br/>"
				+ "<p>We appreciate your business and hope to see you again soon!</p>"
				+ "<p>Best regards,<br/>Luna Aura Team</p>"
				+ "</div>"
				+ "</body>"
				+ "</html>";

		emailSender.sendEmail(recipients, subject, body);
	}


	private void sendThankYouEmail(MultiEntryTicket t) {
		EmailSender emailSender = new EmailSender();
		String[] recipients = {t.getId_user().getEmail()};
		String subject = "Thank You for Your Purchase at Luna Aura";

		String name = t.getId_user().getName();
		String id = t.getId_user().getUser_id();
		LocalDate date = LocalDate.now();
		int paymentAmount = MultiEntryTicket.INITIAL_PRICE;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
		String formattedDate = date.format(formatter);

		String body = "<html>"
				+ "<body style='font-family: Arial, sans-serif; color: #333;'>"
				+ "<div style='max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd;'>"
				+ "<div style='text-align: center;'>"
				+ "<img src='YOUR_LOGO_URL' alt='Luna Aura' style='width: 100px; margin-bottom: 20px;'/>"
				+ "<h1 style='font-size: 24px; color: #555;'>Thank You.</h1>"
				+ "</div>"
				+ "<p>Hi " + name + "!</p>"
				+ "<p>Thanks for your purchase from Luna Aura.</p>"
				+ "<h2 style='color: #555;'>INVOICE ID: " + id + "</h2>"
				+ "<p><em>(Please keep a copy of this receipt for your records.)</em></p>"
				+ "<hr style='border: 0; height: 1px; background-color: #ddd;'/>"
				+ "<h3 style='color: #555;'>YOUR ORDER INFORMATION:</h3>"
				+ "<p><strong>Order ID:</strong> " + id + "<br/>"
				+ "<strong>Order Date:</strong> " + formattedDate + "<br/>"
				+ "<strong>Source:</strong> Luna Aura</p>"
				+ "<h3 style='color: #555;'>HERE'S WHAT YOU ORDERED:</h3>"
				+ "<table style='width: 100%; border-collapse: collapse;'>"
				+ "<thead>"
				+ "<tr>"
				+ "<th style='border-bottom: 1px solid #ddd; padding: 10px; text-align: left;'>Description</th>"
				+ "<th style='border-bottom: 1px solid #ddd; padding: 10px; text-align: left;'>Price</th>"
				+ "</tr>"
				+ "</thead>"
				+ "<tbody>"
				+ "<tr>"
				+ "<td style='padding: 10px; border-bottom: 1px solid #ddd;'>Punch Card for Classes</td>"
				+ "<td style='padding: 10px; border-bottom: 1px solid #ddd;'>₪" + paymentAmount + "</td>"
				+ "</tr>"
				+ "</tbody>"
				+ "</table>"
				+ "<h3 style='color: #555;'>TOTAL [₪]: ₪" + paymentAmount + "</h3>"
				+ "<hr style='border: 0; height: 1px; background-color: #ddd;'/>"
				+ "<p>We appreciate your business and hope to see you again soon!</p>"
				+ "<p>Best regards,<br/>Luna Aura Team</p>"
				+ "</div>"
				+ "</body>"
				+ "</html>";

		emailSender.sendEmail(recipients, subject, body);
	}

	private void customer_service_email(IdUser user,String respond, int price, boolean phase) {
		EmailSender emailSender = new EmailSender();
		String[] recipients = {user.getEmail()};
		String subject;
		if(phase) {
			 subject = "Luna Aura Customer Support Response";
		}
		else {
			subject = "Luna Aura Customer Support Update Response ";
		}

		String name = user.getName();
		LocalDate date = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
		String formattedDate = date.format(formatter);

		String body = "<html>"
				+ "<body style='font-family: Arial, sans-serif; color: #333;'>"
				+ "<div style='max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd;'>"
				+ "<div style='text-align: center;'>"
				+ "<img src='YOUR_LOGO_URL' alt='Luna Aura' style='width: 100px; margin-bottom: 20px;'/>"
				+ "<h1 style='font-size: 24px; color: #555;'>We're Here to Help</h1>"
				+ "</div>"
				+ "<p>Hi " + name + ",</p>"
				+ "<p>Thank you for reaching out to us regarding your recent experience.</p>"
				+ "<p>Here is our response:</p>"
				+ "<div style='border: 1px solid #ccc; padding: 15px; background-color: #f9f9f9;'>"
				+ "<p><strong>Response:</strong> " + respond + "</p>"
				+ "<p><strong>Refund Price:</strong> ₪" + price + "</p>"
				+ "</div>"
				+ "<p>We hope this addresses your concern. If you have any further questions or need additional assistance, please don't hesitate to reach out.</p>"
				+ "<p>Best regards,<br/>Luna Aura Customer Support Team</p>"
				+ "<p style='font-size: 12px; color: #777;'>This email was sent on " + formattedDate + "</p>"
				+ "</div>"
				+ "</body>"
				+ "</html>";

		emailSender.sendEmail(recipients, subject, body);
	}



	private void sendThankYouEmailLink(UserPurchases p1) {
		try {
			if (p1 == null || p1.getId_user() == null) {
				throw new IllegalArgumentException("UserPurchases or associated IdUser is null.");
			}

			EmailSender emailSender = new EmailSender();
			String[] recipients = {p1.getId_user().getEmail()};
			String subject = "Thank You for Your Purchase at Luna Aura";

			String name = p1.getId_user().getName();
			String id = p1.getId_user().getUser_id();
			LocalDate date = LocalDate.now();
			double paymentAmount = p1.getPayment_amount();
			String movie_name = p1.getMovie_name();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
			String formattedDate = date.format(formatter);

			// Retrieve link and wantedDate
			String original_link = "";
			Date durationDate = null;
			List<Movie> data = get_movies_by_name(movie_name);
			for (Movie m : data) {
				if (m.getMovie_name().equals(movie_name)) {
					original_link = m.getMovie_link();
					durationDate = m.getTime_();
				}
			}
			if (original_link.isEmpty() || durationDate == null) {
				return;
			}

			int uniqueNumber = linkCounter.getAndIncrement();

			// extract the begin and start hour
			Date movie_active_date = p1.getDate_of_link_activation();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(movie_active_date);

			// Extract hour and minute from wantedDateObj
			int wantedHour = calendar.get(Calendar.HOUR_OF_DAY);
			int wantedMinute = calendar.get(Calendar.MINUTE);

			Calendar durationCal = Calendar.getInstance();
			durationCal.setTime(durationDate);
			int durationHours = durationCal.get(Calendar.HOUR_OF_DAY);
			int durationMinutes = durationCal.get(Calendar.MINUTE);

			calendar.add(Calendar.HOUR_OF_DAY, durationHours);
			calendar.add(Calendar.MINUTE, durationMinutes);

			// Get the end hour and minute
			int endHour = calendar.get(Calendar.HOUR_OF_DAY);
			int endMinute = calendar.get(Calendar.MINUTE);

			Instant instant = movie_active_date.toInstant(); // Convert Date to Instant
			LocalDate startDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

			httpServer.createContext("/" + p1.getMovie_name() + p1.getId_user().getUser_id() + uniqueNumber,
					new MovieLink(startDate, LocalTime.of(wantedHour, wantedMinute), LocalTime.of(endHour, endMinute), original_link));

			p1.setLink("http://" + SimpleChatServer.host + ":8080/" + p1.getMovie_name() + p1.getId_user().getUser_id() + uniqueNumber);
			String link = p1.getLink();
			Date wantedDate = p1.getDate_of_link_activation();

			// Format wantedDate
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm");
			String formattedWantedDate = (wantedDate != null) ? dateFormat.format(wantedDate) : "N/A";

			// Email body
			String body = "<html>"
					+ "<body style='font-family: Arial, sans-serif; color: #333;'>"
					+ "<div style='max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd;'>"
					+ "<div style='text-align: center;'>"
					+ "<img src='YOUR_LOGO_URL' alt='Luna Aura' style='width: 100px; margin-bottom: 20px;'/>"
					+ "<h1 style='font-size: 24px; color: #555;'>Thank You.</h1>"
					+ "</div>"
					+ "<p>Hi " + name + "!</p>"
					+ "<p>Thanks for your purchase from Luna Aura.</p>"
					+ "<h2 style='color: #555;'>INVOICE ID: " + id + "</h2>"
					+ "<p><em>(Please keep a copy of this receipt for your records.)</em></p>"
					+ "<hr style='border: 0; height: 1px; background-color: #ddd;'/>"
					+ "<h3 style='color: #555;'>YOUR ORDER INFORMATION:</h3>"
					+ "<p><strong>Order ID:</strong> " + id + "<br/>"
					+ "<strong>Order Date:</strong> " + formattedDate + "<br/>"
					+ "<strong>Movie Name:</strong> " + movie_name + "<br/>"
					+ "<strong>Source:</strong> Luna Aura<br/>"
					+ "<strong>Link:</strong> <a href='" + link + "'>" + link + "</a><br/>"
					+ "<strong>Wanted Date:</strong> " + formattedWantedDate + "</p>"
					+ "<h3 style='color: #555;'>HERE'S WHAT YOU ORDERED:</h3>"
					+ "<table style='width: 100%; border-collapse: collapse;'>"
					+ "<thead>"
					+ "<tr>"
					+ "<th style='border-bottom: 1px solid #ddd; padding: 10px; text-align: left;'>Description</th>"
					+ "<th style='border-bottom: 1px solid #ddd; padding: 10px; text-align: left;'>Price</th>"
					+ "</tr>"
					+ "</thead>"
					+ "<tbody>"
					+ "<tr>"
					+ "<td style='padding: 10px; border-bottom: 1px solid #ddd;'>Link Movie: " + movie_name + "</td>"
					+ "<td style='padding: 10px; border-bottom: 1px solid #ddd;'>₪" + paymentAmount + "</td>"
					+ "</tr>"
					+ "</tbody>"
					+ "</table>"
					+ "<h3 style='color: #555;'>TOTAL [₪]: ₪" + paymentAmount + "</h3>"
					+ "<hr style='border: 0; height: 1px; background-color: #ddd;'/>"
					+ "<p>We appreciate your business and hope to see you again soon!</p>"
					+ "<p>Best regards,<br/>Luna Aura Team</p>"
					+ "</div>"
					+ "</body>"
					+ "</html>";

			emailSender.sendEmail(recipients, subject, body);

		} catch (Exception e) {
			// Log the error
			System.err.println("Error sending thank you email: " + e.getMessage());
			e.printStackTrace();
		}
	}


	private String createReminderEmailBody(UserPurchases p1) {
		// Calculate the remaining time until the link activation
		Duration timeUntilActivation = Duration.between(LocalDateTime.now(),
				p1.getDate_of_link_activation().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

		long hoursUntilActivation = timeUntilActivation.toHours();
		long minutesUntilActivation = timeUntilActivation.toMinutes() % 60;

		String timeRemainingMessage;
		if (hoursUntilActivation > 1) {
			timeRemainingMessage = hoursUntilActivation + " hour(s)";
		} else if (hoursUntilActivation == 1) {
			timeRemainingMessage = "1 hour";
		} else {
			timeRemainingMessage = minutesUntilActivation + " minute(s)";
		}

		String body = "<html>"
				+ "<body style='font-family: Arial, sans-serif; color: #333;'>"
				+ "<div style='max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd;'>"
				+ "<div style='text-align: center;'>"
				+ "<img src='YOUR_LOGO_URL' alt='Luna Aura' style='width: 100px; margin-bottom: 20px;'/>"
				+ "<h1 style='font-size: 24px; color: #555;'>Reminder from Luna Aura</h1>"
				+ "</div>"
				+ "<p>Hi " + p1.getId_user().getName() + ",</p>"
				+ "<p>We wanted to remind you about your recent purchase from Luna Aura.</p>"
				+ "<h2 style='color: #555;'>INVOICE ID: " + p1.getId_user().getUser_id() + "</h2>"
				+ "<p><em>(Please keep a copy of this receipt for your records.)</em></p>"
				+ "<hr style='border: 0; height: 1px; background-color: #ddd;'/>"
				+ "<h3 style='color: #555;'>YOUR ORDER INFORMATION:</h3>"
				+ "<p><strong>Order ID:</strong> " + p1.getId_user().getUser_id() + "<br/>"
				+ "<strong>Order Date:</strong> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) + "<br/>"
				+ "<strong>Source:</strong> Luna Aura<br/>"
				+ "<strong>Link:</strong> <a href='" + p1.getLink() + "'>" + p1.getLink() + "</a><br/>"
				+ "<strong>Link Activation Date:</strong> " + (p1.getDate_of_link_activation() != null
				? new SimpleDateFormat("dd MMMM yyyy, HH:mm").format(p1.getDate_of_link_activation())
				: "N/A") + "</p>"
				+ "<p style='color: #FF0000;'><strong>Note:</strong> The link will start working in approximately "
				+ timeRemainingMessage + ".</p>"
				+ "<h3 style='color: #555;'>ORDER SUMMARY:</h3>"
				+ "<table style='width: 100%; border-collapse: collapse;'>"
				+ "<thead>"
				+ "<tr>"
				+ "<th style='border-bottom: 1px solid #ddd; padding: 10px; text-align: left;'>Description</th>"
				+ "<th style='border-bottom: 1px solid #ddd; padding: 10px; text-align: left;'>Price</th>"
				+ "</tr>"
				+ "</thead>"
				+ "<tbody>"
				+ "<tr>"
				+ "<td style='padding: 10px; border-bottom: 1px solid #ddd;'>Link Movie</td>"
				+ "<td style='padding: 10px; border-bottom: 1px solid #ddd;'>₪" + p1.getPayment_amount() + "</td>"
				+ "</tr>"
				+ "</tbody>"
				+ "</table>"
				+ "<h3 style='color: #555;'>TOTAL [₪]: ₪" + p1.getPayment_amount() + "</h3>"
				+ "<hr style='border: 0; height: 1px; background-color: #ddd;'/>"
				+ "<p>Thank you for your attention. If you have any questions, feel free to contact us.</p>"
				+ "<p>Best regards,<br/>Luna Aura Team</p>"
				+ "</div>"
				+ "</body>"
				+ "</html>";

		return body;
	}



	private void update_theater_map(Screening screening)
	{
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(screening);
		session.getTransaction().commit();
		session.close();
	}

	private void saveUpdateIduser(IdUser idUser){
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		if (!session.contains(idUser)) {
			session.saveOrUpdate(idUser);  // This will either save or update depending on the state
		}
		else session.update(idUser);
		session.getTransaction().commit();
		session.close();

	}


	private IdUser getOrSaveIdUser(Session session, IdUser idUser) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<IdUser> idUserQuery = builder.createQuery(IdUser.class);
		Root<IdUser> idUserRoot = idUserQuery.from(IdUser.class);
		idUserQuery.select(idUserRoot).where(builder.equal(idUserRoot.get("user_id"), idUser.getUser_id()));
		IdUser existingIdUser = session.createQuery(idUserQuery).uniqueResult();

		if (existingIdUser != null) {
			return existingIdUser;
		} else {
			session.save(idUser);
			return idUser;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	private boolean handle_submit_complaint(Complains complaint) {
		String id = complaint.getId_user().getUser_id();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		if(complaint.getAuto_number_purchase() != -1) {
			UserPurchases purchases = session.get(UserPurchases.class, complaint.getAuto_number_purchase());
			if(purchases == null || !(purchases.getId_user().getUser_id().equals(id))) {
				return false;
			}
		}
		session.save(complaint);
		session.getTransaction().commit();
		session.close();
		return true;
	}


	private List<Complains> handle_get_user_complaints(String userId) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Complains> query = builder.createQuery(Complains.class);
		Root<Complains> root = query.from(Complains.class);

		// Join with IdUser entity and filter by user_id
		Join<Complains, IdUser> userJoin = root.join("id_user");
		query.select(root).where(builder.equal(userJoin.get("user_id"), userId));

		List<Complains> data = session.createQuery(query).getResultList();

		session.getTransaction().commit();
		session.close();

		return data;
	}


	private List<EditedDetails> getEditedDetails() {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<EditedDetails> query = builder.createQuery(EditedDetails.class);
		query.from(EditedDetails.class);
		List<EditedDetails> data = session.createQuery(query).getResultList();
		session.getTransaction().commit();
		session.close();
		return data;
	}
	private void removeEditedDetails(EditedDetails change) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.delete(change);
		session.getTransaction().commit();
		session.close();
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////// login functions

	// find user by ID
	private IdUser findUserById(Session session, String id) {
		String queryString = "SELECT u FROM IdUser u WHERE u.user_id = :user_id";
		Query<IdUser> query = session.createQuery(queryString, IdUser.class);
		query.setParameter("user_id", id);
		return query.uniqueResult();
	}

	// handle the login logic
	private void handleUserLogin(IdUser user, Session session, Transaction transaction, Message message, ConnectionToClient client) throws IOException {
		if (user == null) {
			message.setMessage("#userNotFound");
			client.sendToClient(message);
		} else if (user.getIsLoggedIn()) {
			message.setMessage("#alreadyLoggedIn");
			client.sendToClient(message);
		} else {
			user.setIsLoggedIn(true);
			session.update(user);
			transaction.commit();
			message.setMessage("#loginConfirmed");
			message.setObject(user);
			client.sendToClient(message);
		}
	}

	// handle exceptions
	private void handleException(Exception e, Transaction transaction, Message message, ConnectionToClient client) {
		if (transaction != null) {
			transaction.rollback();
		}
		message.setMessage("#serverError");
		try {
			client.sendToClient(message);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		e.printStackTrace();
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// reports functions

	// check if reports for these years 2024,2023, 2022 already exist
	private boolean checkAndCreateReports(Session session) {
		int[] years = {2024, 2023, 2022};

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<Reports> root = query.from(Reports.class);
		query.select(builder.count(root));

		Predicate[] yearPredicates = new Predicate[years.length];
		for (int i = 0; i < years.length; i++) {
			Expression<Integer> yearExpr = builder.function("YEAR", Integer.class, root.get("report_date"));
			yearPredicates[i] = builder.equal(yearExpr, years[i]);
		}

		query.where(builder.or(yearPredicates));

		Long reportCount = session.createQuery(query).getSingleResult();

		if (reportCount > 0) {
			System.out.println("Reports already exist for the specified years.");
			return false;
		} else {
			System.out.println("No reports found for the specified years. Proceeding with creation.");
			return true;
		}
	}
	private void create_reports(Session session) {
		// Check if the initialized reports already exist
		if (!checkAndCreateReports(session)) {
			System.out.println("Reports already exist. No need to create new ones.");
			return;
		}
		String[] branches = {"Sakhnin", "Haifa", "Nazareth", "Nhif"};
		int[] years = {2024, 2023, 2022};

		// Maps for storing complaints, purchases, and multi-entry data
		Map<String, Map<Integer, Map<Integer, List<Integer>>>> complaintsByBranchYearMonth = new HashMap<>();
		Map<String, Map<Integer, Map<Integer, List<Integer>>>> purchasesByBranchYearMonth = new HashMap<>();
		Map<String, Map<Integer, Map<Integer, List<Integer>>>> linkPurchasesByBranchYearMonth = new HashMap<>();
		Map<String, Map<Integer, Map<Integer, List<Integer>>>> multiEntryByBranchYearMonth = new HashMap<>();

		// Initialize maps for each branch, year, and month
		for (String branch : branches) {
			complaintsByBranchYearMonth.put(branch, new HashMap<>());
			purchasesByBranchYearMonth.put(branch, new HashMap<>());
			linkPurchasesByBranchYearMonth.put(branch, new HashMap<>());
			multiEntryByBranchYearMonth.put(branch, new HashMap<>());

			for (int year : years) {
				for (int month = 1; month <= 12; month++) {
					complaintsByBranchYearMonth.get(branch)
							.computeIfAbsent(year, k -> new HashMap<>())
							.put(month, initializeEmptyListForMonth(year, month));

					purchasesByBranchYearMonth.get(branch)
							.computeIfAbsent(year, k -> new HashMap<>())
							.put(month, initializeEmptyListForMonth(year, month));

					linkPurchasesByBranchYearMonth.get(branch)
							.computeIfAbsent(year, k -> new HashMap<>())
							.put(month, initializeEmptyListForMonth(year, month));

					multiEntryByBranchYearMonth.get(branch)
							.computeIfAbsent(year, k -> new HashMap<>())
							.put(month, initializeEmptyListForMonth(year, month));
				}
			}
		}

		// Extract and aggregate complaints
		CriteriaBuilder builder1 = session.getCriteriaBuilder();
		CriteriaQuery<Complains> query1 = builder1.createQuery(Complains.class);
		query1.from(Complains.class);
		List<Complains> data_complains = session.createQuery(query1).getResultList();
		for (Complains complains : data_complains) {
			String complain_branch = complains.getCinema_branch();
			Date complain_date = complains.getTime_of_complain();

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(complain_date);

			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			int day = calendar.get(Calendar.DAY_OF_MONTH);

			if (complaintsByBranchYearMonth.containsKey(complain_branch)) {
				List<Integer> dailyComplaints = complaintsByBranchYearMonth.get(complain_branch).get(year).get(month);
				dailyComplaints.set(day - 1, dailyComplaints.get(day - 1) + 1);
			}

		}

		// Extract and aggregate user purchases
		CriteriaBuilder builder2 = session.getCriteriaBuilder();
		CriteriaQuery<UserPurchases> query2 = builder2.createQuery(UserPurchases.class);
		query2.from(UserPurchases.class);
		List<UserPurchases> data_purchases = session.createQuery(query2).getResultList();
		for (UserPurchases userPurchases : data_purchases) {
			String purchases_branch = userPurchases.getScreening().getBranch();
			Date purchases_date = userPurchases.getDate_of_purchase();

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(purchases_date);

			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			double payment_amount = 0;
			if (userPurchases.getPurchase_type().equals("Ticket")){
				payment_amount = userPurchases.getPayment_amount();
				if (purchasesByBranchYearMonth.containsKey(purchases_branch)) {
					List<Integer> dailyPurchases = purchasesByBranchYearMonth.get(purchases_branch).get(year).get(month);
					dailyPurchases.set(day - 1, dailyPurchases.get(day - 1) + (int) payment_amount);
				}
			} else if (userPurchases.getPurchase_type().equals("HomeLink")) {
				payment_amount = userPurchases.getPayment_amount();
				for (String currbranch : BRANCHES){
					List<Integer> dailyLinkPurchases = linkPurchasesByBranchYearMonth.get(currbranch).get(year).get(month);
					dailyLinkPurchases.set(day - 1, dailyLinkPurchases.get(day - 1) + (int) payment_amount);
				}
			}
		}

		// Insert the data from the static map into multiEntryByBranchYearMonth
		for (String branch : branches) {
			for (int year : years) {
				for (int month = 1; month <= 12; month++) {
					List<Integer> dailyMultiEntry = multiEntryByBranchYearMonth.get(branch).get(year).get(month);
					Map<Integer, Integer> monthData = multiEntryTicketSales.get(year).get(month);

					for (int day = 1; day <= monthData.size(); day++) {
						int ticketsSold = monthData.get(day);
						dailyMultiEntry.set(day - 1, dailyMultiEntry.get(day - 1) + ticketsSold); // <-- Change: Aggregating multi-entry data
					}
				}
			}
		}

		List<Reports> reportsList = new ArrayList<>();

		for (String branch : branches) {
			for (int year : years) {
				for (int month = 1; month <= 12; month++) {
					// Generate report for the first day of each month which represent the whole month
					Calendar reportCalendar = Calendar.getInstance();
					reportCalendar.set(Calendar.YEAR, year);
					reportCalendar.set(Calendar.MONTH, month - 1);
					reportCalendar.set(Calendar.DAY_OF_MONTH, 1);
					Date reportDate = reportCalendar.getTime();

					generateReport(session, reportsList, branch, year, month, reportDate,
							complaintsByBranchYearMonth, purchasesByBranchYearMonth,
							linkPurchasesByBranchYearMonth, multiEntryByBranchYearMonth);
				}
			}
		}

	}

	private void generateReport(Session session, List<Reports> reportsList, String branch, int year,
								int month, Date reportDate,
								Map<String, Map<Integer, Map<Integer, List<Integer>>>> complaintsByBranchYearMonth,
								Map<String, Map<Integer, Map<Integer, List<Integer>>>> purchasesByBranchYearMonth,
								Map<String, Map<Integer, Map<Integer, List<Integer>>>> linkPurchasesByBranchYearMonth,
								Map<String, Map<Integer, Map<Integer, List<Integer>>>> multiEntryByBranchYearMonth) {

		Reports report = new Reports(reportDate, branch);

		List<Integer> dailyComplaints = complaintsByBranchYearMonth.get(branch).get(year).get(month);
		List<Integer> dailyPurchases = purchasesByBranchYearMonth.get(branch).get(year).get(month);
		List<Integer> dailyLinkPurchases = linkPurchasesByBranchYearMonth.get(branch).get(year).get(month);
		List<Integer> dailyMultiEntry = multiEntryByBranchYearMonth.get(branch).get(year).get(month);

		report.setReport_complains(dailyComplaints);
		report.setReport_ticket_sells(dailyPurchases);
		report.setReport_link_tickets_sells(dailyLinkPurchases);
		report.setReport_multi_entry_ticket(dailyMultiEntry);

		reportsList.add(report);

		try {
			session.save(report);
			System.out.println("Report saved for branch: " + branch + " on " + reportDate);
		} catch (Exception e) {
			System.err.println("Error saving report for branch: " + branch + " on " + reportDate);
			e.printStackTrace();
		}
	}



	private List<Integer> initializeEmptyListForMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		return new ArrayList<>(Collections.nCopies(daysInMonth, 0));
	}


	private void search_report(Session session, Message message) {
		String branch = (String) message.getObject();
		Integer year = (Integer) message.getObject2();
		Integer month = (Integer) message.getObject3();

		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Reports> query = builder.createQuery(Reports.class);
			Root<Reports> root = query.from(Reports.class);

			Expression<Integer> yearExpr = builder.function("YEAR", Integer.class, root.get("report_date"));
			Expression<Integer> monthExpr = builder.function("MONTH", Integer.class, root.get("report_date"));

			Predicate branchPredicate = builder.equal(root.get("branch"), branch);
			Predicate monthPredicate = builder.equal(monthExpr, month);
			Predicate yearPredicate = builder.equal(yearExpr, year);

			query.select(root).where(builder.and(branchPredicate, monthPredicate, yearPredicate));

			List<Reports> searched_reports = session.createQuery(query).getResultList();

			if (searched_reports != null && !searched_reports.isEmpty()) {
				message.setObject(searched_reports.get(0));
			} else {
				message.setObject(null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void search_report_allBranches(Session session, Message message) {
		Integer year = (Integer) message.getObject2();
		Integer month = (Integer) message.getObject3();

		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Reports> query = builder.createQuery(Reports.class);
			Root<Reports> root = query.from(Reports.class);

			Expression<Integer> yearExpr = builder.function("YEAR", Integer.class, root.get("report_date"));
			Expression<Integer> monthExpr = builder.function("MONTH", Integer.class, root.get("report_date"));

			Predicate monthPredicate = builder.equal(monthExpr, month);
			Predicate yearPredicate = builder.equal(yearExpr, year);

			query.select(root).where(builder.and(monthPredicate, yearPredicate));

			List<Reports> searched_reports = session.createQuery(query).getResultList();

			if (searched_reports != null && !searched_reports.isEmpty()) {
				message.setObject(searched_reports);
			} else {
				message.setObject(null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void deleteAllReports(Session session) {
		System.out.println("Deleting all existing reports...");
		session.createQuery("DELETE FROM Reports").executeUpdate();
	}

	private void resetAutoIncrement(Session session) {
		System.out.println("Resetting auto-increment...");
		session.createNativeQuery("ALTER TABLE Reports AUTO_INCREMENT = 1").executeUpdate();
	}

	// get num of days in a month
	private static int getDaysInMonth(int month, int year) {
		switch (month) {
			case 2: // February
				return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 29 : 28; // Leap year check
			case 4:
			case 6:
			case 9:
			case 11: // April, June, September, November
				return 30;
			default: // January, March, May, July, August, October, December
				return 31;
		}
	}

	// Function to update the map for multi-entry tickets
	private synchronized void updateReportsForMultiEntryTickets(int remainTickets) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		multiEntryTicketSales
				.computeIfAbsent(year, k -> new HashMap<>())
				.computeIfAbsent(month, k -> new HashMap<>())
				.computeIfAbsent(day, k -> 0);

		int currentTickets = multiEntryTicketSales.get(year).get(month).get(day);
		multiEntryTicketSales.get(year).get(month).put(day, currentTickets + remainTickets);
	}

	private void update_reports() {
		Session session = sessionFactory.openSession();
		Transaction transaction = null;
		Calendar today = Calendar.getInstance();
		int currentYear = today.get(Calendar.YEAR);
		int currentMonth = today.get(Calendar.MONTH) + 1; // Months are 0-based in Calendar
		int currentDay = today.get(Calendar.DAY_OF_MONTH);
		List<Reports> changed_reports = new ArrayList<Reports>();
		try {
			transaction = session.beginTransaction();
			if (reports_message.getMessage().equals("purchaseMultiTicket")) {
				System.out.println("got into update report for purchaseMultiTicket");
				MultiEntryTicket ticket = (MultiEntryTicket) reports_message.getObject();
				int remain_tickets = (int) ticket.INITIAL_PRICE;

				for (String branch : SimpleServer.BRANCHES) {
					Reports report = getReportForCurrentDay(session, currentYear, currentMonth, currentDay, branch);
					List<Integer> multiEntryData = report.getReport_multi_entry_ticket();
					multiEntryData.set(currentDay - 1, multiEntryData.get(currentDay - 1) + remain_tickets);
					report.setReport_multi_entry_ticket(multiEntryData);
					session.update(report);
					changed_reports.add(report);
				}

			} else if (reports_message.getMessage().equals("cancelPurchase")) {
				System.out.println("got into update report for cancelPurchase");
				UserPurchases purchase = (UserPurchases) reports_message.getObject();
				double refund = (double) reports_message.getObject2();
				Date date_ = purchase.getDate_of_purchase();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date_);
				currentDay = calendar.get(Calendar.DAY_OF_MONTH);
				currentMonth = calendar.get(Calendar.MONTH) + 1;
				currentYear = calendar.get(Calendar.YEAR);
				if (purchase.getPurchase_type().equals("Ticket")){
					Reports report = getReportForCurrentDay(session, currentYear, currentMonth, currentDay, purchase.getScreening().getBranch());
					List<Integer> purchaseData = report.getReport_ticket_sells();
					int originalPurchase = purchaseData.get(currentDay - 1);
					purchaseData.set(currentDay - 1, originalPurchase - (int) refund);
					report.setReport_ticket_sells(purchaseData);
					session.update(report);
					changed_reports.add(report);
				}
				else if (purchase.getPurchase_type().equals("HomeLink")){
					for (String branch : BRANCHES) {
						Reports report = getReportForCurrentDay(session, currentYear, currentMonth, currentDay, branch);
						List<Integer> LinkPurchaseData = report.getReport_link_tickets_sells();
						int originalLinkPurchase = LinkPurchaseData.get(currentDay - 1);
						LinkPurchaseData.set(currentDay - 1, originalLinkPurchase - (int) refund);
						report.setReport_link_tickets_sells(LinkPurchaseData);
						session.update(report);
						changed_reports.add(report);
					}
				}
			}
			else if (reports_message.getMessage().equals("extraComplaint")){
				System.out.println("got into update report for extraComplaint");
				Complains complain = (Complains) reports_message.getObject();
				if (complain.getCinema_branch().equals("All")) {
					for (String branch : BRANCHES) {
						Reports report = getReportForCurrentDay(session, currentYear, currentMonth, currentDay, branch);
						List<Integer> complainData = report.getReport_complains();
						int original = complainData.get(currentDay - 1);
						complainData.set(currentDay - 1, original + 1);
						report.setReport_complains(complainData);
						session.update(report);
						changed_reports.add(report);
					}
				}
				else{
					Reports report = getReportForCurrentDay(session, currentYear, currentMonth, currentDay,complain.getCinema_branch());
					List<Integer> complainData = report.getReport_complains();
					int original = complainData.get(currentDay - 1);
					complainData.set(currentDay - 1, original + 1);
					report.setReport_complains(complainData);
					session.update(report);
					changed_reports.add(report);
				}
			}
			else if (reports_message.getMessage().equals("ExtraPurchase")){
				System.out.println("got into update report for ExtraPurchase");
				UserPurchases purchase = (UserPurchases) reports_message.getObject();
				double price = (double) purchase.getPayment_amount();
				Reports report = getReportForCurrentDay(session, currentYear, currentMonth, currentDay, purchase.getScreening().getBranch());
				List<Integer> purchaseData = report.getReport_ticket_sells();
				int originalPurchase = purchaseData.get(currentDay - 1);
				purchaseData.set(currentDay - 1, originalPurchase + (int) price);
				report.setReport_ticket_sells(purchaseData);

				session.update(report);
				changed_reports.add(report);
			}
			else if (reports_message.getMessage().equals("extraLinkPurchase")){
				System.out.println("got into update report for extraLinkPurchase");
				UserPurchases purchase = (UserPurchases) reports_message.getObject();
				double price = purchase.getPayment_amount();

				for (String branch : BRANCHES) {
					Reports report = getReportForCurrentDay(session, currentYear, currentMonth, currentDay, branch);
					List<Integer> linkPurchaseData = report.getReport_link_tickets_sells();
					int originalPurchase = linkPurchaseData.get(currentDay - 1);
					linkPurchaseData.set(currentDay - 1, originalPurchase + (int) price);
					report.setReport_link_tickets_sells(linkPurchaseData);
					session.update(report);
					changed_reports.add(report);
				}
			}

			reports_message.setMessage("updatedReports");
			reports_message.setObject(changed_reports);

			transaction.commit();

		} catch (Exception e) {
			if (session.getTransaction() != null) {
				session.getTransaction().rollback();
			}
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	private Reports getReportForCurrentDay(Session session, int year, int month, int day, String branch) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Reports> query = builder.createQuery(Reports.class);
		Root<Reports> root = query.from(Reports.class);

		query.select(root)
				.where(builder.equal(root.get("branch"), branch),
						builder.equal(builder.function("YEAR", Integer.class, root.get("report_date")), year),
						builder.equal(builder.function("MONTH", Integer.class, root.get("report_date")), month));

		return session.createQuery(query).uniqueResult();
	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Message message = (Message) msg;
		String request = message.getMessage();

		try {
			////////////// Get All Movies /////////////
			if (message.getMessage().equals("#GetAllMovies")) {

				List<Movie> movies = getAllMovies();
				message.setObject(movies);
				message.setMessage("#GotAllMovies");
				client.sendToClient(message);
			}

			////////////// Search Movie Using Filter  /////////////
			else if (message.getMessage().equals("#SearchMovieFillter")) {
				Movie m = (Movie)message.getObject();
				Map<String, String> dictionary = (Map<String, String>) message.getObject2();
				List<Movie> answer = search_with_filter(m,Integer.parseInt(dictionary.get("year2")),dictionary.get("Sort_atribute"),dictionary.get("Sort_direction"));
				message.setObject(answer);
				message.setMessage("#GotSearchMovieFillter");
				client.sendToClient(message);

			}

			////////////// Delete Movie ///////////////////////////
			else if (message.getMessage().equals("#DeleteMovie")) {
				Movie movie = (Movie) message.getObject();
				remove_movie(movie);
				message.setObject(getAllMovies());
				message.setMessage("#UpdateMovieList");
				sendToAllClients(message);
			}

			////////////// Go To Screening //////////////////////
			else if (message.getMessage().equals("#GoToScreenings")) {
				Movie movie = (Movie) message.getObject();
				System.out.println("screening number");
				System.out.println(movie.getScreenings().size());
				message.setObject(movie.getScreenings());
				message.setMessage("#ScreeningsGot");
				client.sendToClient(message);
			}

			else if (message.getMessage().equals("#GetScreening")) {
				Movie movie = (Movie) message.getObject();


				message.setObject(getScreeningForMovie(movie));
				message.setMessage("#GetScreeningDone");
				client.sendToClient(message);

			}

			else if (message.getMessage().equals("#InsertMovie")) {
				Movie movie = (Movie) message.getObject();
				insert_movie(movie);
				message.setObject(getAllMovies());
				message.setMessage("#UpdateMovieList");
				sendToAllClients(message);
				Message message1 = new Message(10, "#ChangeMovieIdBox");
				message1.setObject(movie);
				client.sendToClient(message1);
			}

			else if (message.getMessage().equals("#UpdateMovie")) {
				Movie movie = (Movie) message.getObject();
				update_movie(movie);
				message.setObject(getAllMovies());
				message.setMessage("#UpdateMovieList");
				sendToAllClients(message);
			}

			else if (message.getMessage().equals("#SearchMovies")) {
				String movieName = (String) message.getObject();
				message.setObject(get_movies_by_name(movieName));
				message.setMessage("#UpdateMovieList_Eatch");
				client.sendToClient(message);
			}

			else if (message.getMessage().equals("#AddNewScreening")) {
				Screening screening = (Screening) message.getObject();
				boolean add = check_the_new_screening(screening, false);
				if (add) {
					add_new_screening(screening);
					message.setMessage("#UpdateScreeningForMovie");
					message.setObject2(screening.getMovie());
					message.setObject(get_screening_for_movie(screening.getMovie()));

					sendToAllClients(message);
					Message message1 = new Message(20, "#UpdateBoxesInScreening");
					message1.setObject(screening);

					client.sendToClient(message1);
				}

				else {
					message.setMessage("#ServerError");
					message.setData("there is already a screening at this time");
					client.sendToClient(message);
				}
			}

			else if (message.getMessage().equals("#get_screening_from_id")) {
				int screening_id = (Integer) message.getObject();
				message.setObject(get_screening(screening_id));
				message.setMessage("#UpdateBoxesInScreening");
				client.sendToClient(message);
			}


			else if (message.getMessage().equals("#RemoveScreening")) {
				Movie movie = ((Screening) message.getObject()).getMovie();
				Screening screening = (Screening) message.getObject();
				remove_screening(screening);
				message.setObject(get_screening_for_movie(movie));
				message.setObject2(movie);
				message.setMessage("#UpdateScreeningForMovie");
				sendToAllClients(message);
			}


			else if (message.getMessage().equals("#SearchBranchForScreening")) {
				Movie movie = (Movie) message.getObject();
				String Branch = (String) message.getObject2();
				List<Screening> screenings = search_sreening_branch_and_movie(Branch, movie);
				message.setObject(screenings);
				message.setObject2(movie);
				message.setMessage("#UpdateScreeningForMovie_each");
				client.sendToClient(message);
			}

			else if (message.getMessage().equals("#UpdateScreening")) {
				Movie movie = ((Screening) message.getObject()).getMovie();
				Screening screening = (Screening) message.getObject();
				screening.setMovie(movie);
				boolean add = check_the_new_screening(screening, true);
				if (add) {
					update_screening(screening);
					message.setObject(get_screening_for_movie(movie));
					message.setObject2(movie);
					message.setMessage("#UpdateScreeningForMovie");
					sendToAllClients(message);
				} else {
					message.setMessage("#ServerError");
					message.setData("there is already a screening at this time");
					client.sendToClient(message);
				}

			}

			else if (message.getMessage().equals("#ChangeAllPrices")) {
				int new_price = (int) message.getObject();
				update_all_prices(new_price);
				message.setMessage("#UpdateMovieList");
				message.setObject(getAllMovies());
				client.sendToClient(message);

			}

			else if (message.getMessage().equals("#show_purchases"))
			{

				String id = (String) message.getObject();

				message.setMessage("#show_purchases_client");
				System.out.println(message.getMessage());
				List<UserPurchases> data = search_user_purchases(id);
				int remain_multi_tickets = get_remain_tickets(id);
				message.setObject(data);
				message.setObject2(remain_multi_tickets);

				client.sendToClient(message);


			}

			else if (message.getMessage().equals("#delete_purchases")) {
				int auto_num =  (int)message.getObject();
				String id = (String)message.getObject2();
				message.setMessage("#delete_purchases_client");
				message.setObject(delete_user_purchases(auto_num,id, message));
				System.out.println(message.getMessage());
				client.sendToClient(message);
				sendToAllClients(reports_message);

			}

			else if (message.getMessage().equals("#return_tickets")) {
				Session session = sessionFactory.openSession();
				session.beginTransaction();
				int auto_num =  (int)message.getObject();
				int num_of_seats=(int)message.getObject2();
				UserPurchases p1 = (UserPurchases) getPurchaseByAuto(auto_num);
				IdUser user = p1.getId_user();
				MultiEntryTicket t1= getMultiTicketUsingIdUser_not_list(user);
				t1.setRemain_tickets(t1.getRemain_tickets()+num_of_seats);
				session.update(t1);
				session.getTransaction().commit();
				session.close();
				message.setMessage("#ADD_Multi_tickets_client");
				message.setObject(t1.getRemain_tickets());
				client.sendToClient(message);

			}

			else if (message.getMessage().equals("#PayMultiTicket")) {
				System.out.println("We are now in the server yo ");

				String id = (String)message.getObject();
				System.out.println(id);

				IdUser idUser = (IdUser)message.getObject3();
				System.out.println(idUser.getUser_id());

				IdUser idUser_from_base = getIUFromId(id);

				if(idUser_from_base != null) {
					idUser.setIsLoggedIn(idUser_from_base.getIsLoggedIn());
					idUser.setAuto_number_id_user(idUser_from_base.getAuto_number_id_users());
				}
				else{
					message.setMessage("#FailedMT");
					System.out.println("Errorrrrrrrrrrrrrr");
					client.sendToClient(message);
					return;
				}

				//check if found:
				saveUpdateIduser(idUser);

				idUser_from_base = idUser;



				int seats_num = (int) message.getObject2();
				System.out.println("Seats number: " + seats_num);

				List <MultiEntryTicket> multiEntryTicketList = getMultiTicketUsingIdUser(idUser_from_base);

				if (multiEntryTicketList != null) {
					for (MultiEntryTicket multiEntryTicket : multiEntryTicketList) {

						System.out.println(multiEntryTicket.getId_user().getUser_id());
						System.out.println("remaining tickets: " +multiEntryTicket.getRemain_tickets());

						if (multiEntryTicket.getRemain_tickets() >= seats_num) {
							multiEntryTicket.setRemain_tickets(multiEntryTicket.getRemain_tickets() - seats_num);
							// update the database (function)
							updateMT(multiEntryTicket);
							message.setMessage("#DonePayMultiTicket");
							//to do: add to the purchases data


							//also send an email

							message.setObject(idUser);

							client.sendToClient(message);
							break;
						}

						else {
							message.setMessage("#FailedMT");
							System.out.println("Errorrrrrrrrrrrrrr");
							client.sendToClient(message);
						}
					}
				}

				else {
					message.setMessage("#FailedMT");
					System.out.println("Errorrrrrrrrrrrrrr");
					client.sendToClient(message);
				}


			}


			else if (message.getMessage().equals("#Save_user_purchases")) {
				UserPurchases userPurchases = (UserPurchases) message.getObject();
				IdUser idUser = userPurchases.getId_user();
				System.out.println(userPurchases.getSeats());
				saveUP(userPurchases);
				message.setMessage("#SavedUserPurchases");
				reports_message.setObject(userPurchases);
				reports_message.setMessage("ExtraPurchase");
				update_reports();
				//sendToAllClients(message);
				client.sendToClient(message);
				sendToAllClients(reports_message);
			}

			else if (message.getMessage().equals("#Success_CC")){
				String id = (String)message.getObject();
				IdUser idUser = (IdUser)message.getObject2();
				IdUser idUser_from_base = getIUFromId(id);
				if(idUser_from_base != null ) {
					idUser.setIsLoggedIn(idUser_from_base.getIsLoggedIn());
					idUser.setAuto_number_id_user(idUser_from_base.getAuto_number_id_users());
				}
				saveUpdateIduser(idUser);


				message.setMessage("#DonePayCC");
				message.setObject(idUser);
				client.sendToClient(message);
				//to do: add to the purchases data

			}

			else if(message.getMessage().equals("#Send_mail")){
				UserPurchases userPurchases = (UserPurchases)message.getObject();
				sendEmail1(userPurchases);
				message.setMessage("#Done_Sending_email");
				client.sendToClient(message);
			}


			else if (message.getMessage().equals("#LogIn_worker")) {
				try {
					Session session = sessionFactory.openSession();
					session.beginTransaction();

					String userName = (String) message.getObject();
					String password = (String) message.getObject2();


					Query query = session.createQuery("FROM Worker WHERE user_name = :userName");
					query.setParameter("userName", userName);
					Worker worker = (Worker) query.uniqueResult();

					if (worker == null) {
						message.setMessage("#loginWorkerFailedUserName");
						client.sendToClient(message);
					} else if (worker.getPassword().equals(password)) {
						if(worker.getIs_worker_loggedIn()) {
							message.setMessage("#alreadylogin");
						}
						else {
							message.setMessage("#loginWorker");
							worker.setIs_worker_loggedIn(true);
							session.update(worker);
						}
						message.setObject(worker);
						client.sendToClient(message);

					} else {
						message.setMessage("#loginWorkerFailedPass");
						client.sendToClient(message);
					}

					// Commit the transaction
					session.getTransaction().commit();
					session.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

			}

			else if (message.getMessage().equals("#GetHomePage")) {
				SubscribedClient connection = new SubscribedClient(client);
				if (SubscribersList.contains(connection) == false) {
					SubscribersList.add(connection);

				}
				List<Movie> movies = get_near_movies();
				message.setMessage("#GoToHomePage");
				message.setObject(movies);
				client.sendToClient(message);
			}

			//////////////////////////////////////////////////////Purchase Part /////////////////////////////////////////////////////////

			else if (message.getMessage().equals("#purchase_movie_link")) {
					UserPurchases p1 = (UserPurchases) message.getObject();
					IdUser user1 = getOrSaveIdUser(p1.getId_user());

			try (Session session = sessionFactory.openSession()) {
				Transaction transaction = session.beginTransaction();
				p1.setId_user(user1);
				session.save(p1);
				message.setMessage("#purchase_movie_link_client");
				message.setObject("");
				client.sendToClient(message);
				reports_message.setObject(p1);
				reports_message.setMessage("extraLinkPurchase");
				update_reports();
				sendToAllClients(reports_message);
				sendThankYouEmailLink(p1);
				Date sendTime = p1.getDate_of_link_activation();
				EmailScheduler emailScheduler = new EmailScheduler();
				emailScheduler.scheduleEmail(
						p1.getId_user().getEmail(),
						"Schedule the  Thank You for Your Purchase at Luna Aura",
						createReminderEmailBody(p1),
						sendTime
				);

				transaction.commit();
				session.close();

			}


			catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error while saving movie link: " + e.getMessage());
			}
			}
			else if (message.getMessage().equals("#purchase_movie_link_by_multi_ticket")) {
					message.setMessage("#purchase_movie_link_by_multi_ticket_client");
					UserPurchases p1 = (UserPurchases) message.getObject();
					IdUser user1 = getOrSaveIdUser(p1.getId_user());

				try (Session session = sessionFactory.openSession()) {
					Transaction transaction = session.beginTransaction();

					CriteriaBuilder builder = session.getCriteriaBuilder();
					CriteriaQuery<MultiEntryTicket> query = builder.createQuery(MultiEntryTicket.class);
					Root<MultiEntryTicket> root = query.from(MultiEntryTicket.class);
					Join<MultiEntryTicket, IdUser> userJoin = root.join("id_user");  // Join with IdUser
					Predicate userIdPredicate = builder.equal(userJoin.get("user_id"), user1.getUser_id());
					query.select(root).where(userIdPredicate);
					MultiEntryTicket ticket = session.createQuery(query).uniqueResult();
					if (ticket != null) {
						if (ticket.getRemain_tickets() == 0) {
							message.setObject("Your Multi Ticket is Empty.");
							client.sendToClient(message);
						} else {
							p1.setId_user(user1);
							session.save(p1);
							ticket.setRemain_tickets(ticket.getRemain_tickets() - 1);
							session.update(ticket);

							message.setObject("Purchase Success! Your remaining ticket count is " + ticket.getRemain_tickets());
							client.sendToClient(message);

							sendThankYouEmailLink(p1);
							Date sendTime = p1.getDate_of_link_activation();
							EmailScheduler emailScheduler = new EmailScheduler();
							emailScheduler.scheduleEmail(
									p1.getId_user().getEmail(),
									"Thank You for Your Purchase at Luna Aura",
									createReminderEmailBody(p1),
									sendTime
							);
						}
					} else {
						System.out.println("No MultiEntryTicket found for the given user.");
						message.setObject("No MultiEntryTicket found for the given user.");
						client.sendToClient(message);
					}
					transaction.commit();
					session.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error while saving movie link: " + e.getMessage());
				}
			}
			else if (message.getMessage().equals("#purchase_multi_ticket")) {
                try (Session session = sessionFactory.openSession()) {
                    Transaction transaction = session.beginTransaction();
                    MultiEntryTicket t = (MultiEntryTicket) message.getObject();
                    IdUser idUser = getOrSaveIdUser( t.getId_user());
                    transaction.commit();
                    session.close();
                    updateOrSaveMultiEntryTicket(t, idUser);
                    sendThankYouEmail(t);
                    message.setMessage("#purchase_multi_ticket_client");
                    client.sendToClient(message);

					//updateReportsForMultiEntryTickets(t.getRemain_tickets());    // used for reports
                    reports_message.setObject(t);
                    reports_message.setMessage("purchaseMultiTicket");
                    update_reports();
					sendToAllClients(reports_message);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error while purchase_multi_ticket : " + e.getMessage());
                }
            }

//////////////////////////////////////////////   Complains Part /////////////////////////////////////////////////////////
			else if (message.getMessage().equals("#GetUserComplaints")) {
				//System.out.println("get user complaints");
				String current_id = (String) message.getObject();
				message.setMessage("#ShowUserComplaints");
				List<Complains> current_complaints = handle_get_user_complaints(current_id);
				message.setObject(current_complaints);
				client.sendToClient(message);
				//System.out.println("sent user complaints");
			} else if (message.getMessage().equals("#SubmitComplaint")) {
				//System.out.println("submit user complaint");
				Complains complaint = (Complains) message.getObject();
				boolean suc = handle_submit_complaint(complaint);
				if(!suc)
				{
					message.setMessage("#incorrect_purchase_number");
					client.sendToClient(message);
					return;
				}
				String current_id = complaint.getId_user().getUser_id();
				message.setMessage("#ShowUserComplaints");
				List<Complains> updated_complaints = handle_get_user_complaints(current_id);
				message.setObject(updated_complaints);
				reports_message.setObject(complaint);
				reports_message.setMessage("extraComplaint");
				update_reports();
				client.sendToClient(message);
				sendToAllClients(reports_message);
			} else if (message.getMessage().equals("#GetCMEditedDetails")) {
				//System.out.println("get cme details");
				List<EditedDetails> editedDetailsList = getEditedDetails();
				message.setObject(editedDetailsList);
				message.setMessage("#ShowCMEditedDetails");
				client.sendToClient(message);
			} else if (message.getMessage().equals("#UpdateMoviePrice")) {
				EditedDetails change = (EditedDetails) message.getObject();
				update_movie(change.getMovie());
				removeEditedDetails(change);
				List<EditedDetails> editedDetailsList = getEditedDetails();
				message.setObject(editedDetailsList);
				message.setMessage("#ShowCMEditedDetails");
				sendToAllClients(message);
			} else if (message.getMessage().equals("#DenyMoviePrice")) {
				EditedDetails change = (EditedDetails) message.getObject();
				removeEditedDetails(change);
				List<EditedDetails> editedDetailsList = getEditedDetails();
				message.setObject(editedDetailsList);
				message.setMessage("#ShowCMEditedDetails");
				sendToAllClients(message);
			}
			else if (message.getMessage().equals("#show_complains")){
				// set massage
				message.setMessage("#show_complains_for_client");

				// delete the responded complains
				List<Complains> data = search_data(true);

				message.setObject(data);
				// send to client
				client.sendToClient(message);
			}
			else if (message.getMessage().equals("#show_respond")){
				// set massage
				message.setMessage("#show_respond_complains_for_client");

				// delete the responded complains
				List<Complains> data = search_data(false);
				message.setObject(data);
				// send to client
				client.sendToClient(message);
			}
			else if (message.getMessage().equals("#submit_respond")) {

				String respondText = (String)((List<Object>)message.getObject()).get(0);
				boolean phase = (boolean) ((List<Object>)message.getObject()).get(1);

				int number = (int)message.getObject2();
				int returned_price = (int)message.getObject3();
				///////////////////////////////////////
				/// aisha u can update the reports here
				//////////////////////////////////////

				List<Complains> data = update_respond(number, respondText, phase);

				Session session = sessionFactory.openSession();
				session.beginTransaction();

				// Find the object with the specified auto_num
				Complains complains = session.get(Complains.class, number);
				customer_service_email(complains.getId_user(),respondText, returned_price, phase);

				session.getTransaction().commit();
				session.close();

				message.setMessage("#submit_respond_for_client");
				// delete the responded complains
				message.setObject(data);
				client.sendToClient(message);
			}
			else if(message.getMessage().equals("#Update_theater_map")){
				message.setMessage("#theater_map_updated");
				update_theater_map((Screening) message.getObject());
				sendToAllClients(message);

			}
			else if (message.getMessage().equals("#login")) {
				Session session = sessionFactory.openSession();
				Transaction transaction = session.beginTransaction();

				try {
					String id = message.getObject2().toString();
					IdUser user = findUserById(session, id);

					handleUserLogin(user, session, transaction, message, client);

				} catch (Exception e) {
					handleException(e, transaction, message, client);
				} finally {
					session.close();
				}
			}
			else if (message.getMessage().equals("#SignOut_UserID") || message.getMessage().equals("#SignOut_Worker")) {
				Object user = message.getObject();
				if (user instanceof IdUser) {
					SignOut_IDUser((IdUser) user);
				}
				else if (user instanceof Worker) {
					SignOut_Worker((Worker)user);
				}

			} else if (message.getMessage().equals("#createReports")) {
				System.out.println("got into createReports (simpleServer)");
				Session session = sessionFactory.openSession();
				Transaction transaction = session.beginTransaction();
				create_reports(session);
				message.setMessage("#reportsCreated");
				client.sendToClient(message);
				session.getTransaction().commit();
				session.close();
			}
			else if (message.getMessage().equals("#SearchReport")) {
				System.out.println("got into SearchReport (simpleServer)");
				Session session = sessionFactory.openSession();
				Transaction transaction = session.beginTransaction();
				search_report(session, message);
				message.setMessage("#searchedReports");
				client.sendToClient(message);
				transaction.commit();
				session.close();
			}
			else if (message.getMessage().equals("#deleteAllReports")) {
				System.out.println("got into deleteAllReports (simpleServer)");
				Session session = sessionFactory.openSession();
				Transaction transaction = session.beginTransaction();
				deleteAllReports(session);
				resetAutoIncrement(session);
				message.setMessage("#reportsDeleted");;
				client.sendToClient(message);
				transaction.commit();
				session.close();
			}
			else if (message.getMessage().equals("#SearchReportForAllBranches")){
				System.out.println("got into SearchReportForAllBranches (simpleServer)");
				Session session = sessionFactory.openSession();
				Transaction transaction = session.beginTransaction();
				search_report_allBranches(session, message);
				message.setMessage("#searchedReportsForAllBranches");
				client.sendToClient(message);
				transaction.commit();
				session.close();
			}
			else if (message.getMessage().equals("#Log_out_user"))
			{
				System.out.println("I'm here 12 12 ");
				IdUser user = (IdUser) message.getObject();
				SignOut_IDUser(user);
			} else if (message.getMessage().equals("#Log_out_worker")) {
				Worker worker = (Worker) message.getObject();
				SignOut_Worker(worker);
			}
			else if (message.getMessage().equals("#get_purchase_info")) {
				int purchase_num = (int) message.getObject();
				Session session = sessionFactory.openSession();
				Transaction transaction = session.beginTransaction();
				UserPurchases purchase = session.get(UserPurchases.class, purchase_num);
				if(purchase == null)
				{
					message.setMessage("#not_fond_purchase_info_client");
					client.sendToClient(message);
				}
				message.setMessage("#get_purchase_info_client");
				message.setObject(purchase);
				client.sendToClient(message);
				transaction.commit();
				session.close();
			}


		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void sendToAllClients(Message message) {

		try {
			for (SubscribedClient SubscribedClient : SubscribersList) {
				SubscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}