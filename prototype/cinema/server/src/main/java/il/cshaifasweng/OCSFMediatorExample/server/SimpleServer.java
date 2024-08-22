package il.cshaifasweng.OCSFMediatorExample.server;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.ZoneId;

import java.util.*;
import il.cshaifasweng.OCSFMediatorExample.server.EmailSender;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.criteria.Join;
import java.time.LocalDate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import com.mysql.cj.xdevapi.Client;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.io.Serializable;
import java.security.PrivateKey;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Iterator;

import java.util.Collections;
import il.cshaifasweng.OCSFMediatorExample.entities.MultiEntryTicket;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private static SessionFactory sessionFactory = getSessionFactory(SimpleChatServer.password);



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
	private List<UserPurchases> delete_user_purchases(int auto_num,String id) throws Exception {
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

	private void delete_purchase(UserPurchases purchase) throws Exception {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.delete(purchase);
		session.getTransaction().commit();
		session.close();
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

	private void sendThankYouEmailLink(UserPurchases p1) {
		try {
			// Ensure p1 and its associated IdUser are properly managed
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

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
			String formattedDate = date.format(formatter);

			// Retrieve link and wantedDate
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
					+ "<td style='padding: 10px; border-bottom: 1px solid #ddd;'>Link Movie</td>"
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
	/////////////////////////////   Handle Messages  /////////////////////////////////////////////////////////////////////////////////////

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
				message.setObject(data);

				client.sendToClient(message);


			}

			else if (message.getMessage().equals("#delete_purchases")) {
				int auto_num =  (int)message.getObject();
				String id = (String)message.getObject2();
				message.setMessage("#delete_purchases_client");
				message.setObject(delete_user_purchases(auto_num,id));
				System.out.println(message.getMessage());
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
						message.setMessage("#loginWorker");
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
					p1.setPayment_type("Credit");
					IdUser user1 = getOrSaveIdUser(p1.getId_user());

				try (Session session = sessionFactory.openSession()) {
					Transaction transaction = session.beginTransaction();
					p1.setId_user(user1);
					session.save(p1);
					message.setMessage("#purchase_movie_link_client");
					message.setObject("");
					client.sendToClient(message);
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
					p1.setPayment_type("Crtesea");
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
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error while purchase_multi_ticket : " + e.getMessage());
				}
			}

//////////////////////////////////////////////   Complains Part /////////////////////////////////////////////////////////
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
				List<Complains> data = update_respond(number, respondText, phase);

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
					String queryString1 = "SELECT u FROM IdUser u WHERE u.user_id = :user_id";
					Query<IdUser> query1 = session.createQuery(queryString1, IdUser.class);
					String id = message.getObject2().toString();
					query1.setParameter("user_id", id);
					IdUser user = query1.uniqueResult();

					if (user == null) {
						message.setMessage("#userNotFound");
						client.sendToClient(message);
					} else {
						if (user.getIsLoggedIn()) {
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
				} catch (Exception e) {
					if (transaction != null) {
						transaction.rollback();
					}
					message.setMessage("#serverError");
					client.sendToClient(message);
					e.printStackTrace();
				} finally {
					session.close();
				}
			}
			else if (message.getMessage().equals("#SignOut_UserID")) {
				Object user = message.getObject();
				if (user instanceof IdUser) {
					SignOut_IDUser((IdUser) user);
				}
				else if (user instanceof Worker) {
					SignOut_Worker((Worker)user);
				}

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