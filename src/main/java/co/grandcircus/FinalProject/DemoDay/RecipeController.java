package co.grandcircus.FinalProject.DemoDay;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.grandcircus.FinalProject.DemoDay.dao.MenuItemDao;
import co.grandcircus.FinalProject.DemoDay.dao.UserDao;
import co.grandcircus.FinalProject.DemoDay.entity.Favorite;
import co.grandcircus.FinalProject.DemoDay.entity.Result;
import co.grandcircus.FinalProject.DemoDay.entity.User;

@Controller
public class RecipeController {

	@Autowired
	private MenuItemDao menuItemDao;

	@Autowired
	private UserDao userDao;

	private LocalDate dateToday;

	// shows login page
	@RequestMapping("/")
	public ModelAndView showIndex() {
		ModelAndView mav = new ModelAndView("index");
		return mav;
	}

	// display the registration page
	@RequestMapping("/register")
	public ModelAndView showRegistration() {
		ModelAndView mav = new ModelAndView("register");
		return mav;
	}

	// Adds user information to database
	@PostMapping("/register")
	public ModelAndView showRegistration(@RequestParam("first_name") String first_name,
			@RequestParam("last_name") String last_name, @RequestParam("password") String password,
			@RequestParam("email") String email, @RequestParam("password2") String password2, RedirectAttributes redir) {

		String passwordTest;
		String duplicateEmail;

		if (userDao.findByEmail(email) != null) {
			duplicateEmail = "This email already exist, please enter a valid email.";
			ModelAndView mav = new ModelAndView("register");
			mav.addObject("first_name", first_name);
			mav.addObject("last_name", last_name);
			mav.addObject("duplicateEmail", duplicateEmail);
			return mav;
		} else {

			if (password.matches(password2)) {

				passwordTest = "";

				User user = new User();
				user.setFirst_name(first_name);
				user.setLast_name(last_name);
				user.setPassword(password);
				user.setEmail(email);

				userDao.create(user);
				return new ModelAndView("redirect:/login");

			}

			else {
				passwordTest = "Passwords entered do not match. Please try again.";
				ModelAndView mav = new ModelAndView("register");
				mav.addObject("email", email);
				mav.addObject("first_name", first_name);
				mav.addObject("last_name", last_name);
				mav.addObject("passwordTest", passwordTest);
				return mav;
			}
		}
	}

	// shows empty search box with day and time availability
	@RequestMapping("/display/{date}")
	public ModelAndView showSearch(@SessionAttribute("user") User user, 
			@PathVariable("date") String date, @RequestParam("time") int time,
			@RequestParam("searchType") String searchType) {

		LocalDate searchDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM-dd-uuuu"));
		DayOfWeek day = searchDate.getDayOfWeek();
		ModelAndView mav = new ModelAndView("display");
		mav.addObject("time", time);
		mav.addObject("day", day);
		mav.addObject("searchType", searchType);
		return mav;
	}

	// calls API to search using user's keyword and time availability
	// ("/display/{searchType}/{time}/{date}")
	@RequestMapping("/display/{searchType}/{time}/{date}")
	public ModelAndView showList(@SessionAttribute("user") User user, 
			@RequestParam(value = "keyword", required = false) String keyword,
			@PathVariable("searchType") String searchType, @PathVariable("time") int time,
			@PathVariable("date") String date) {

		ModelAndView mav = new ModelAndView("display");
		mav.addObject("searchType", searchType);
		if (keyword != null) {
			mav.addObject("keyword", keyword);
		}
		RestTemplate restTemplate = new RestTemplate();

		if (searchType.equals("favorites")) {
			List<Favorite> favorites = menuItemDao.findByUser(user);
			mav.addObject("favorites", favorites);
			mav.addObject("date", date);
		}

		else {

			String url = "https://api.edamam.com/search?q=" + keyword + "&app_id=328dd333"
					+ "&app_key=2925530f7873bcd09aa1376f5114f08d" + "&from=0&to=10" // optional: limits number of
																					// results
					+ "&time=1-" + time; // total time is between 1 and maxTotalTime

			// call to API
			ResponseEntity<Result> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null),
					Result.class);

			// extract results from API response
			Result result = response.getBody();

			// extract recipes from results, label as "recipelist" for use in jsp
			mav.addObject("recipelist", result.getHits());
			mav.addObject("date", date);
		}

		return mav;

	}

	// show calendar beginning on following Sunday, includes meals added
	@RequestMapping("/calendar")
	public ModelAndView showCalendar(@SessionAttribute("user") User user) {
		ModelAndView mav = new ModelAndView("calendar");

		dateToday = LocalDate.now();

		DayOfWeek currentDay = dateToday.getDayOfWeek();

		LocalDate sunday = null;
		LocalDate monday = null;
		LocalDate tuesday = null;
		LocalDate wednesday = null;
		LocalDate thursday = null;
		LocalDate friday = null;
		LocalDate saturday = null;

		switch (currentDay) {
		case SUNDAY:
			sunday = dateToday;
			monday = dateToday.plusDays(1);
			tuesday = dateToday.plusDays(2);
			wednesday = dateToday.plusDays(3);
			thursday = dateToday.plusDays(4);
			friday = dateToday.plusDays(5);
			saturday = dateToday.plusDays(6);
			break;
		case MONDAY:
			sunday = dateToday.plusDays(6);
			monday = dateToday.plusDays(7);
			tuesday = dateToday.plusDays(8);
			wednesday = dateToday.plusDays(9);
			thursday = dateToday.plusDays(10);
			friday = dateToday.plusDays(11);
			saturday = dateToday.plusDays(12);
			break;
		case TUESDAY:
			sunday = dateToday.plusDays(5);
			monday = dateToday.plusDays(6);
			tuesday = dateToday.plusDays(7);
			wednesday = dateToday.plusDays(8);
			thursday = dateToday.plusDays(9);
			friday = dateToday.plusDays(10);
			saturday = dateToday.plusDays(11);
			break;
		case WEDNESDAY:
			sunday = dateToday.plusDays(4);
			monday = dateToday.plusDays(5);
			tuesday = dateToday.plusDays(6);
			wednesday = dateToday.plusDays(7);
			thursday = dateToday.plusDays(8);
			friday = dateToday.plusDays(9);
			saturday = dateToday.plusDays(10);
			break;
		case THURSDAY:
			sunday = dateToday.plusDays(3);
			monday = dateToday.plusDays(4);
			tuesday = dateToday.plusDays(5);
			wednesday = dateToday.plusDays(6);
			thursday = dateToday.plusDays(7);
			friday = dateToday.plusDays(8);
			saturday = dateToday.plusDays(9);
			break;
		case FRIDAY:
			sunday = dateToday.plusDays(2);
			monday = dateToday.plusDays(3);
			tuesday = dateToday.plusDays(4);
			wednesday = dateToday.plusDays(5);
			thursday = dateToday.plusDays(6);
			friday = dateToday.plusDays(7);
			saturday = dateToday.plusDays(8);
			break;
		case SATURDAY:
			sunday = dateToday.plusDays(1);
			monday = dateToday.plusDays(2);
			tuesday = dateToday.plusDays(3);
			wednesday = dateToday.plusDays(4);
			thursday = dateToday.plusDays(5);
			friday = dateToday.plusDays(6);
			saturday = dateToday.plusDays(7);
			break;
		default:
			break;
		}
		
		String sundayString = sunday.format(DateTimeFormatter.ofPattern("MM-dd-uuuu"));
		String mondayString = monday.format(DateTimeFormatter.ofPattern("MM-dd-uuuu"));
		String tuesdayString = tuesday.format(DateTimeFormatter.ofPattern("MM-dd-uuuu"));
		String wednesdayString = wednesday.format(DateTimeFormatter.ofPattern("MM-dd-uuuu"));
		String thursdayString = thursday.format(DateTimeFormatter.ofPattern("MM-dd-uuuu"));
		String fridayString = friday.format(DateTimeFormatter.ofPattern("MM-dd-uuuu"));
		String saturdayString = saturday.format(DateTimeFormatter.ofPattern("MM-dd-uuuu"));
		
		mav.addObject("sunday", sundayString);
		mav.addObject("monday", mondayString);
		mav.addObject("tuesday", tuesdayString);
		mav.addObject("wednesday", wednesdayString);
		mav.addObject("thursday", thursdayString);
		mav.addObject("friday", fridayString);
		mav.addObject("saturday", saturdayString);

		if (menuItemDao.findByDate(sundayString) != null) {
			mav.addObject("sundayMeal",
					menuItemDao.findByDate(sundayString));
		}

		if (menuItemDao.findByDate(mondayString) != null) {
			mav.addObject("mondayMeal",
					menuItemDao.findByDate(mondayString));
		}

		if (menuItemDao.findByDate(tuesdayString) != null) {
			mav.addObject("tuesdayMeal",
					menuItemDao.findByDate(tuesdayString));
		}

		if (menuItemDao.findByDate(wednesdayString) != null) {
			mav.addObject("wednesdayMeal",
					menuItemDao.findByDate(wednesdayString));
		}

		if (menuItemDao.findByDate(thursdayString) != null) {
			mav.addObject("thursdayMeal",
					menuItemDao.findByDate(thursdayString));
		}

		if (menuItemDao.findByDate(fridayString) != null) {
			mav.addObject("fridayMeal",
					menuItemDao.findByDate(fridayString));
		}

		if (menuItemDao.findByDate(saturdayString) != null) {
			mav.addObject("saturdayMeal",
					menuItemDao.findByDate(saturdayString));
		}

		return mav;
	}

	// user adds recipe to database from API search
	@PostMapping("/add-to-menu/{date}")
	public ModelAndView addRecipeToMenu(@SessionAttribute("user") User user,
			@RequestParam("label") String label, 
			@RequestParam("image") String image,
			@RequestParam("url") String url,
			@RequestParam("ingredientLines") String [] ingredientLines,
			@PathVariable("date") String date, RedirectAttributes redir) {

		Favorite favorite = new Favorite();
		favorite.setLabel(label);
		favorite.setMealDate(date);
		String[] imageArray = image.split(",");
		favorite.setImage(imageArray[0]);
		String[] urlArray = url.split(",");
		favorite.setUrl(urlArray[0]);
		String ingr = Arrays.toString(ingredientLines);
		favorite.setIngredientLines(ingr);
		menuItemDao.create(favorite);
		
		ModelAndView mav = new ModelAndView("redirect:/calendar");
		redir.addFlashAttribute("message", "Item added to favorites!");

		return mav;
	}

	@RequestMapping("/login")
	public ModelAndView showLoginForm() {
		return new ModelAndView("login-form");
	}

	@PostMapping("/login")
	// get the username and password from the form when it's submitted.
	public ModelAndView submitLoginForm(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpSession session, RedirectAttributes redir) {
		// Find the matching user.
		User user = userDao.findByEmail(email);
		if (user == null || !password.equals(user.getPassword())) {
			// If the user or password don't match, display an error message.
			ModelAndView mav = new ModelAndView("login-form");
			mav.addObject("message", "Incorrect username or password.");
			return mav;
		}

		// On successful login, add the user to the session.
		session.setAttribute("user", user);

		// A flash message will only show on the very next page. Then it will go away.
		// It is useful with redirects since you can't add attributes to the mav.
		redir.addFlashAttribute("message", "Logged in.");
		return new ModelAndView("redirect:/calendar");
	}

	@RequestMapping("/logout")
	public ModelAndView logout(HttpSession session, RedirectAttributes redir) {
		// invalidate clears the current user session and starts a new one.
		session.invalidate();

		// A flash message will only show on the very next page. Then it will go away.
		// It is useful with redirects since you can't add attributes to the mav.
		redir.addFlashAttribute("message", "Logged out.");
		return new ModelAndView("redirect:/");
	}

	@RequestMapping("/shoppingcart")
	public ModelAndView showCart(@SessionAttribute("user") User user) {
		ModelAndView mav = new ModelAndView("shoppingcart");
		return mav;
	}

}
