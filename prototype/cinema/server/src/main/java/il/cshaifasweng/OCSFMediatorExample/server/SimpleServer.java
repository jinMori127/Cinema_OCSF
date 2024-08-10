package il.cshaifasweng.OCSFMediatorExample.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.persistence.criteria.Join;

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

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private static SessionFactory sessionFactory = getSessionFactory(SimpleChatServer.password);

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

	}
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

	private List<Reports> create_reports(Session session) {
		String[] branches = {"Sakhnin", "Haifa", "Nazareth", "Nhif"};
		int[] years = {2024, 2023, 2022};

		// Maps for storing complaints, purchases, and multi-entry data
		Map<String, Map<Integer, Map<Integer, List<Integer>>>> complaintsByBranchYearMonth = new HashMap<>();
		Map<String, Map<Integer, Map<Integer, List<Integer>>>> purchasesByBranchYearMonth = new HashMap<>();
		Map<String, Map<Integer, Map<Integer, List<Integer>>>> multiEntryByBranchYearMonth = new HashMap<>();

		// Initialize maps for each branch, year, and month
		for (String branch : branches) {
			complaintsByBranchYearMonth.put(branch, new HashMap<>());
			purchasesByBranchYearMonth.put(branch, new HashMap<>());
			multiEntryByBranchYearMonth.put(branch, new HashMap<>());

			for (int year : years) {
				for (int month = 1; month <= 12; month++) {
					complaintsByBranchYearMonth.get(branch)
							.computeIfAbsent(year, k -> new HashMap<>())
							.put(month, initializeEmptyListForMonth(year, month));

					purchasesByBranchYearMonth.get(branch)
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
			String seats = userPurchases.getSeats();
			int num_seats = seats.isEmpty() ? 0 : seats.split(",").length;

			if (purchasesByBranchYearMonth.containsKey(purchases_branch)) {
				List<Integer> dailyPurchases = purchasesByBranchYearMonth.get(purchases_branch).get(year).get(month);
				dailyPurchases.set(day - 1, dailyPurchases.get(day - 1) + num_seats);
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
							multiEntryByBranchYearMonth);
				}
			}
		}

		return reportsList;
	}

	private void generateReport(Session session, List<Reports> reportsList, String branch, int year,
								int month, Date reportDate,
								Map<String, Map<Integer, Map<Integer, List<Integer>>>> complaintsByBranchYearMonth,
								Map<String, Map<Integer, Map<Integer, List<Integer>>>> purchasesByBranchYearMonth,
								Map<String, Map<Integer, Map<Integer, List<Integer>>>> multiEntryByBranchYearMonth) {

		Reports report = new Reports(reportDate, branch);

		List<Integer> dailyComplaints = complaintsByBranchYearMonth.get(branch).get(year).get(month);
		List<Integer> dailyPurchases = purchasesByBranchYearMonth.get(branch).get(year).get(month);
		List<Integer> dailyMultiEntry = multiEntryByBranchYearMonth.get(branch).get(year).get(month);

		report.setReport_complains(dailyComplaints);
		report.setReport_ticket_sells(dailyPurchases);
		report.setReport_multy_entry_ticket(dailyMultiEntry);

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




	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Message message = (Message) msg;
		String request = message.getMessage();

		try {
			if (message.getMessage().equals("#GetAllMovies")) {

				List<Movie> movies = getAllMovies();
				message.setObject(movies);
				message.setMessage("#GotAllMovies");
				client.sendToClient(message);
			} else if (message.getMessage().equals("#SearchMovieFillter")) {
				Movie m = (Movie)message.getObject();
				Map<String, String> dictionary = (Map<String, String>) message.getObject2();
				List<Movie> answer = search_with_filter(m,Integer.parseInt(dictionary.get("year2")),dictionary.get("Sort_atribute"),dictionary.get("Sort_direction"));
				message.setObject(answer);
				message.setMessage("#GotSearchMovieFillter");
				client.sendToClient(message);

			} else if (message.getMessage().equals("#DeleteMovie")) {
				Movie movie = (Movie) message.getObject();
				remove_movie(movie);
				message.setObject(getAllMovies());
				message.setMessage("#UpdateMovieList");
				sendToAllClients(message);
			} else if (message.getMessage().equals("#GoToScreenings")) {
				Movie movie = (Movie) message.getObject();
				System.out.println("screening number");
				System.out.println(movie.getScreenings().size());
				message.setObject(movie.getScreenings());
				message.setMessage("#ScreeningsGot");
				client.sendToClient(message);

			} else if (message.getMessage().equals("#InsertMovie")) {
				Movie movie = (Movie) message.getObject();
				insert_movie(movie);
				message.setObject(getAllMovies());
				message.setMessage("#UpdateMovieList");
				sendToAllClients(message);
				Message message1 = new Message(10, "#ChangeMovieIdBox");
				message1.setObject(movie);
				client.sendToClient(message1);
			} else if (message.getMessage().equals("#UpdateMovie")) {
				Movie movie = (Movie) message.getObject();
				update_movie(movie);
				message.setObject(getAllMovies());
				message.setMessage("#UpdateMovieList");
				sendToAllClients(message);
			} else if (message.getMessage().equals("#SearchMovies")) {
				String movieName = (String) message.getObject();
				message.setObject(get_movies_by_name(movieName));
				message.setMessage("#UpdateMovieList_Eatch");
				client.sendToClient(message);
			} else if (message.getMessage().equals("#AddNewScreening")) {
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
				} else {
					message.setMessage("#ServerError");
					message.setData("there is already a screening at this time");
					client.sendToClient(message);
				}
			} else if (message.getMessage().equals("#get_screening_from_id")) {
				int screening_id = (Integer) message.getObject();
				message.setObject(get_screening(screening_id));
				message.setMessage("#UpdateBoxesInScreening");
				client.sendToClient(message);
			} else if (message.getMessage().equals("#RemoveScreening")) {
				Movie movie = ((Screening) message.getObject()).getMovie();
				Screening screening = (Screening) message.getObject();
				remove_screening(screening);
				message.setObject(get_screening_for_movie(movie));
				message.setObject2(movie);
				message.setMessage("#UpdateScreeningForMovie");
				sendToAllClients(message);
			} else if (message.getMessage().equals("#SearchBranchForScreening")) {
				Movie movie = (Movie) message.getObject();
				String Branch = (String) message.getObject2();
				List<Screening> screenings = search_sreening_branch_and_movie(Branch, movie);
				message.setObject(screenings);
				message.setObject2(movie);
				message.setMessage("#UpdateScreeningForMovie_each");
				client.sendToClient(message);
			} else if (message.getMessage().equals("#UpdateScreening")) {
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

			} else if (message.getMessage().equals("#ChangeAllPrices")) {
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

					// Use HQL to fetch the Worker object by user_name
					// Use HQL to fetch the Worker object by user_name
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

			} else if (message.getMessage().equals("#GetHomePage")) {
				SubscribedClient connection = new SubscribedClient(client);
				if (SubscribersList.contains(connection) == false) {
					SubscribersList.add(connection);

				}
				List<Movie> movies = get_near_movies();
				message.setMessage("#GoToHomePage");
				message.setObject(movies);
				client.sendToClient(message);
			}

			else if (message.getMessage().equals("#purchase_multi_ticket")) {

				MultiEntryTicket t = (MultiEntryTicket) message.getObject();
				IdUser idUser = t.getId_user(); // Adjust according to your getter method

				Session session = null;
				Transaction transaction = null;

				try {
					session = sessionFactory.openSession();
					transaction = session.beginTransaction();

					// Check if IdUser already exists in the database
					CriteriaBuilder builder = session.getCriteriaBuilder();
					CriteriaQuery<IdUser> idUserQuery = builder.createQuery(IdUser.class);
					Root<IdUser> idUserRoot = idUserQuery.from(IdUser.class);
					idUserQuery.select(idUserRoot).where(builder.equal(idUserRoot.get("user_id"), idUser.getUser_id()));
					IdUser existingIdUser = session.createQuery(idUserQuery).uniqueResult();

					if (existingIdUser != null) {
						// If IdUser already exists, use the existing instance
						idUser = existingIdUser;
					} else {
						// If IdUser does not exist, save it
						session.save(idUser);
					}

					// Check if MultiEntryTicket already exists
					CriteriaQuery<MultiEntryTicket> query = builder.createQuery(MultiEntryTicket.class);
					Root<MultiEntryTicket> root = query.from(MultiEntryTicket.class);
					query.select(root).where(builder.equal(root.get("id_user"), idUser));
					MultiEntryTicket t2 = session.createQuery(query).uniqueResult();

					if (t2 != null) {
						t2.setRemain_tickets(t2.getRemain_tickets() + t.getRemain_tickets());
						session.update(t2);
					} else {
						t.setId_user(idUser); // Ensure the ticket references the existing IdUser
						session.save(t);
					}
					updateReportsForMultiEntryTickets(t.getRemain_tickets());	// used for reports

					transaction.commit();

					message.setMessage("#purchase_multi_ticket_client");
					client.sendToClient(message);

				} catch (Exception e) {
					if (transaction != null) {
						transaction.rollback();
					}
					e.printStackTrace();
					System.out.println("Error while saving MultiEntryTicket: " + e.getMessage());
				} finally {
					if (session != null) {
						session.close();
					}
				}
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
				List<Complains> data = update_respond(number, respondText, phase);

				message.setMessage("#submit_respond_for_client");
				// delete the responded complains
				message.setObject(data);
				client.sendToClient(message);
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
			else if (message.getMessage().equals("#SignOut_UserID")) {
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
				List<Reports> respond = create_reports(session);
				message.setObject(respond);
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