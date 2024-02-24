import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/Utilities")

/* 
	Utilities class contains class variables of type HttpServletRequest, PrintWriter,String and HttpSession.

	Utilities class has a constructor with  HttpServletRequest, PrintWriter variables.
	  
*/

public class Utilities extends HttpServlet{
	HttpServletRequest req;
	PrintWriter pw;
	String url;
	HttpSession session; 
	public Utilities(HttpServletRequest req, PrintWriter pw) {
		this.req = req;
		this.pw = pw;
		this.url = this.getFullURL();
		this.session = req.getSession(true);
	}



	/*  Printhtml Function gets the html file name as function Argument, 
		If the html file name is Header.html then It gets Username from session variables.
		Account ,Cart Information ang Logout Options are Displayed*/

	public void printHtml(String file) {
		String result = HtmlToString(file);
		//to print the right navigation in header of username cart and logout etc
		if (file == "Header.html") {
				result=result+"<div id='menu' style='float: right;'><ul>";
			if (session.getAttribute("username")!=null){
				String username = session.getAttribute("username").toString();
				username = Character.toUpperCase(username.charAt(0)) + username.substring(1);

				String userType = session.getAttribute("usertype").toString();
				switch (userType) {
					case "customer":
						result = result + "<li><a><span class='glyphicon'>Hello, " + username + "</span></a></li>"
								+ "<li><a href='Account'><span class='glyphicon'>Account</span></a></li>"
								+ "<li><a href='Logout'><span class='glyphicon'>Logout</span></a></li>";
						break;
					case "retailer":
					result = result + "<li><a href='ProductModify?button=Addproduct'><span class='glyphicon'>Addproduct</span></a></li>"
						+ "<li><a href='ProductModify?button=Updateproduct'><span class='glyphicon'>Updateproduct</span></a></li>"
						+"<li><a href='ProductModify?button=Deleteproduct'><span class='glyphicon'>Deleteproduct</span></a></li>"
						// +"<li><a href='DataVisualization'><span class='glyphicon'>Trending</span></a></li>"
						+ "<li><a><span class='glyphicon'>Hello, " + username + "</span></a></li>"
						+ "<li><a href='Logout'><span class='glyphicon'>Logout</span></a></li>";
						break;
					case "manager":
						result = result + "<li><a href='SalesmanHome'><span class='glyphicon'>ViewOrder</span></a></li>"
								+ "<li><a><span class='glyphicon'>Hello, " + username + "</span></a></li>"
								+ "<li><a href='Logout'><span class='glyphicon'>Logout</span></a></li>";
						break;
					}
				}
			else
				result = result +"<li><a href='ViewOrder'><span class='glyphicon'>View Order</span></a></li>"+ "<li><a href='Login'><span class='glyphicon'>Login</span></a></li>";
				result = result +"<li><a href='Cart'><span class='glyphicon'>Cart("+CartCount()+")</span></a></li></ul></div></div><div id='page'>";
				pw.print(result);
		} else
				pw.print(result);
	}
	

	/*  getFullURL Function - Reconstructs the URL user request  */

	public String getFullURL() {
		String scheme = req.getScheme();
		String serverName = req.getServerName();
		int serverPort = req.getServerPort();
		String contextPath = req.getContextPath();
		StringBuffer url = new StringBuffer();
		url.append(scheme).append("://").append(serverName);

		if ((serverPort != 80) && (serverPort != 443)) {
			url.append(":").append(serverPort);
		}
		url.append(contextPath);
		url.append("/");
		return url.toString();
	}

	/*  HtmlToString - Gets the Html file and Converts into String and returns the String.*/
	public String HtmlToString(String file) {
		String result = null;
		try {
			String webPage = url + file;
			URL url = new URL(webPage);
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			result = sb.toString();
		} 
		catch (Exception e) {
		}
		return result;
	} 

	/*  logout Function removes the username , usertype attributes from the session variable*/

	public void logout(){
		session.removeAttribute("username");
		session.removeAttribute("usertype");
	}
	
	/*  logout Function checks whether the user is loggedIn or Not*/

	public boolean isLoggedin(){
		if (session.getAttribute("username")==null)
			return false;
		return true;
	}

	/*  username Function returns the username from the session variable.*/
	
	public String username(){
		if (session.getAttribute("username")!=null)
			return session.getAttribute("username").toString();
		return null;
	}
	
	/*  usertype Function returns the usertype from the session variable.*/
	public String usertype(){
		if (session.getAttribute("usertype")!=null)
			return session.getAttribute("usertype").toString();
		return null;
	}
	
	/*  getUser Function checks the user is a customer or retailer or manager and returns the user class variable.*/
	public User getUser(){
		String usertype = usertype();
		HashMap<String, User> hm=new HashMap<String, User>();
		//String TOMCAT_HOME = System.getProperty("catalina.home");
			try
			{		
				// FileInputStream fileInputStream=new FileInputStream(new File(TOMCAT_HOME+"\\webapps\\Assignment_1\\UserDetails.txt"));
				// ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);	      
				// hm= (HashMap)objectInputStream.readObject();
				hm = MySqlDataStoreUtilities.selectUser();
			}
			catch(Exception e)
			{
			}	
		User user = hm.get(username());
		return user;
	}
	
	/*  getCustomerOrders Function gets  the Orders for the user*/
	public ArrayList<OrderItem> getCustomerOrders(){
		ArrayList<OrderItem> order = new ArrayList<OrderItem>(); 
		if(OrdersHashMap.orders.containsKey(username()))
			order= OrdersHashMap.orders.get(username());
		return order;
	}

	/*  getOrdersPaymentSize Function gets  the size of OrderPayment */
	public int getOrderPaymentSize(){
		HashMap<Integer, ArrayList<OrderPayment>> orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();
		// String TOMCAT_HOME = System.getProperty("catalina.home");
			try
			{
				// FileInputStream fileInputStream = new FileInputStream(new File(TOMCAT_HOME+"\\webapps\\Assignment_1\\PaymentDetails.txt"));
				// ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);	      
				// orderPayments = (HashMap)objectInputStream.readObject();
				orderPayments =MySqlDataStoreUtilities.selectOrder();
			}
			catch(Exception e)
			{
			
			}
			int size=0;
			for(Map.Entry<Integer, ArrayList<OrderPayment>> entry : orderPayments.entrySet()){
					 size=size + 1;
					 
			}
			return size;		
	}

	/*  CartCount Function gets  the size of User Orders*/
	public int CartCount(){
		if(isLoggedin())
		return getCustomerOrders().size();
		return 0;
	}

	//delete product
	public void removeItemFromCart(String itemName) {
	ArrayList<OrderItem> orderItems = OrdersHashMap.orders.get(username());
	int index = 0;
	for (OrderItem oi : orderItems) {
		if (oi.getName().equals(itemName)) {
			break;
		} else index++;
	}
	orderItems.remove(index);
	}

	//OrdersHashMap.orders.values(name);
	//Create Products
	public boolean createProduct(String id, String name, String price, String condition, String discount, String image, String type) {
		switch (type) {
			case "fitness":
				FitnessWatch fitness = new FitnessWatch();
				fitness.setId(id);
				fitness.setName(name);
				fitness.setPrice(Double.parseDouble(price));
			//  fitness.setRetailer(manufacturer);
				fitness.setCondition(condition);
				fitness.setDiscount(Double.parseDouble(discount));
				fitness.setImage(image);
				SaxParserDataStore.fitnesswatches.remove(id);
				SaxParserDataStore.fitnesswatches.put(id, fitness);

				return true;
			case "smartwatch":

				SmartWatch smartWatch = new SmartWatch();
				smartWatch.setId(id);
				smartWatch.setName(name);
				smartWatch.setPrice(Double.parseDouble(price));
				//smartWatch.setRetailer(manufacturer);
				smartWatch.setCondition(condition);
				smartWatch.setDiscount(Double.parseDouble(discount));
				smartWatch.setImage(image);
				SaxParserDataStore.smartwatches.remove(id);
				SaxParserDataStore.smartwatches.put(id, smartWatch);
				return true;
			case "tv":

				Tv tv = new Tv();
				tv.setId(id);
				tv.setName(name);
				tv.setPrice(Double.parseDouble(price));
			//  tv.setRetailer(manufacturer);
				tv.setCondition(condition);
				tv.setDiscount(Double.parseDouble(discount));
				tv.setImage(image);
				SaxParserDataStore.tvs.remove(id);
				SaxParserDataStore.tvs.put(id, tv);
				return true;
			case "soundsystem":

				SoundSystem soundsystem = new SoundSystem();
				soundsystem.setId(id);
				soundsystem.setName(name);
				soundsystem.setPrice(Double.parseDouble(price));
			//  soundsystem.setRetailer(manufacturer);
				soundsystem.setCondition(condition);
				soundsystem.setDiscount(Double.parseDouble(discount));
				soundsystem.setImage(image);
				SaxParserDataStore.soundsystems.remove(id);
				SaxParserDataStore.soundsystems.put(id, soundsystem);
				return true;

			case "headphones":

				HeadPhone headphone = new HeadPhone();
				headphone.setId(id);
				headphone.setName(name);
				headphone.setPrice(Double.parseDouble(price));
			//  headphone.setRetailer(manufacturer);
				headphone.setCondition(condition);
				headphone.setDiscount(Double.parseDouble(discount));
				headphone.setImage(image);
				SaxParserDataStore.headphones.remove(id);
				SaxParserDataStore.headphones.put(id, headphone);
				return true;
			case "phone":

				Phone phone = new Phone();
				phone.setId(id);
				phone.setName(name);
				phone.setPrice(Double.parseDouble(price));
			//  phone.setRetailer(manufacturer);
				phone.setCondition(condition);
				phone.setDiscount(Double.parseDouble(discount));
				phone.setImage(image);
				SaxParserDataStore.phones.remove(id);
				SaxParserDataStore.phones.put(id, phone);
				return true;
			case "laptop":

				Laptop laptop = new Laptop();
				laptop.setId(id);
				laptop.setName(name);
				laptop.setPrice(Double.parseDouble(price));
			//  laptop.setRetailer(manufacturer);
				laptop.setCondition(condition);
				laptop.setDiscount(Double.parseDouble(discount));
				laptop.setImage(image);
				SaxParserDataStore.laptops.remove(id);
				SaxParserDataStore.laptops.put(id, laptop);
				return true;
			case "voiceassistant":

				VoiceAssistant voiceassistant = new VoiceAssistant();
				voiceassistant.setId(id);
				voiceassistant.setName(name);
				voiceassistant.setPrice(Double.parseDouble(price));
				//speaker.setRetailer(manufacturer);
				voiceassistant.setCondition(condition);
				voiceassistant.setDiscount(Double.parseDouble(discount));
				voiceassistant.setImage(image);
				SaxParserDataStore.voiceassistants.remove(id);
				SaxParserDataStore.voiceassistants.put(id, voiceassistant);
				return true;

			case "accessory":

				Accessory accessory = new Accessory();
				accessory.setId(id);
				accessory.setName(name);
				accessory.setPrice(Double.parseDouble(price));
				//accessory.setRetailer(manufacturer);
				accessory.setCondition(condition);
				accessory.setDiscount(Double.parseDouble(discount));
				accessory.setImage(image);
				SaxParserDataStore.accessories.remove(id);
				SaxParserDataStore.accessories.put(id, accessory);
				return true;

		}
		return false;
	}

	public boolean updateProduct(String id, String name, String price, String condition, String discount, String image, String type) {
        switch (type) {
		case "fitness":
		
			FitnessWatch fitness = new FitnessWatch();
			fitness.setId(id);
			fitness.setName(name);
			fitness.setPrice(Double.parseDouble(price));
		//  fitness.setRetailer(manufacturer);
			fitness.setCondition(condition);
			fitness.setDiscount(Double.parseDouble(discount));
			fitness.setImage(image);
			SaxParserDataStore.fitnesswatches.remove(id);
			SaxParserDataStore.fitnesswatches.put(id, fitness);

                return true;
            case "smartwatch":

				SmartWatch smartWatch = new SmartWatch();
				smartWatch.setId(id);
				smartWatch.setName(name);
				smartWatch.setPrice(Double.parseDouble(price));
				//smartWatch.setRetailer(manufacturer);
				smartWatch.setCondition(condition);
				smartWatch.setDiscount(Double.parseDouble(discount));
				smartWatch.setImage(image);
				SaxParserDataStore.smartwatches.remove(id);
				SaxParserDataStore.smartwatches.put(id, smartWatch);
				return true;

            case "tv":

				Tv tv = new Tv();
				tv.setId(id);
				tv.setName(name);
				tv.setPrice(Double.parseDouble(price));
			//  tv.setRetailer(manufacturer);
				tv.setCondition(condition);
				tv.setDiscount(Double.parseDouble(discount));
				tv.setImage(image);
				SaxParserDataStore.tvs.remove(id);
				SaxParserDataStore.tvs.put(id, tv);
				return true;

			case "soundsystem":

				SoundSystem soundsystem = new SoundSystem();
				soundsystem.setId(id);
				soundsystem.setName(name);
				soundsystem.setPrice(Double.parseDouble(price));
			//  soundsystem.setRetailer(manufacturer);
				soundsystem.setCondition(condition);
				soundsystem.setDiscount(Double.parseDouble(discount));
				soundsystem.setImage(image);
				SaxParserDataStore.soundsystems.remove(id);
				SaxParserDataStore.soundsystems.put(id, soundsystem);
				return true;
				
            case "headphones":

				HeadPhone headphone = new HeadPhone();
				headphone.setId(id);
				headphone.setName(name);
				headphone.setPrice(Double.parseDouble(price));
			//  headphone.setRetailer(manufacturer);
				headphone.setCondition(condition);
				headphone.setDiscount(Double.parseDouble(discount));
				headphone.setImage(image);
				SaxParserDataStore.headphones.remove(id);
				SaxParserDataStore.headphones.put(id, headphone);
				return true;

            case "phone":

				Phone phone = new Phone();
				phone.setId(id);
				phone.setName(name);
				phone.setPrice(Double.parseDouble(price));
			//  phone.setRetailer(manufacturer);
				phone.setCondition(condition);
				phone.setDiscount(Double.parseDouble(discount));
				phone.setImage(image);
				SaxParserDataStore.phones.remove(id);
				SaxParserDataStore.phones.put(id, phone);
				return true;

            case "laptop":

				Laptop laptop = new Laptop();
				laptop.setId(id);
				laptop.setName(name);
				laptop.setPrice(Double.parseDouble(price));
			//  laptop.setRetailer(manufacturer);
				laptop.setCondition(condition);
				laptop.setDiscount(Double.parseDouble(discount));
				laptop.setImage(image);
				SaxParserDataStore.laptops.remove(id);
				SaxParserDataStore.laptops.put(id, laptop);
				return true;

            case "voiceassistant":

				VoiceAssistant voiceassistant = new VoiceAssistant();
				voiceassistant.setId(id);
				voiceassistant.setName(name);
				voiceassistant.setPrice(Double.parseDouble(price));
				//speaker.setRetailer(manufacturer);
				voiceassistant.setCondition(condition);
				voiceassistant.setDiscount(Double.parseDouble(discount));
				voiceassistant.setImage(image);
				SaxParserDataStore.voiceassistants.remove(id);
				SaxParserDataStore.voiceassistants.put(id, voiceassistant);
				return true;		
				
			case "accessory":

				Accessory accessory = new Accessory();
				accessory.setId(id);
				accessory.setName(name);
				accessory.setPrice(Double.parseDouble(price));
				//accessory.setRetailer(manufacturer);
				accessory.setCondition(condition);
				accessory.setDiscount(Double.parseDouble(discount));
				accessory.setImage(image);
				SaxParserDataStore.accessories.remove(id);
				SaxParserDataStore.accessories.put(id, accessory);
				return true;

        }
        return false;
    }	
	/* StoreProduct Function stores the Purchased product in Orders HashMap according to the User Names.*/
	public boolean removeProduct(String productId, String catalog) {
		switch (catalog) {
			case "fitness":
				SaxParserDataStore.fitnesswatches.remove(productId);
				return true;


			case "smartwatch":

				SaxParserDataStore.smartwatches.remove(productId);
				return true;

			case "tv":

				SaxParserDataStore.tvs.remove(productId);
				return true;

			case "soundsystem":

				SaxParserDataStore.soundsystems.remove(productId);
				return true;

			case "headphones":

				SaxParserDataStore.headphones.remove(productId);
				return true;

			case "phone":

				SaxParserDataStore.phones.remove(productId);
				return true;

			case "laptop":

				SaxParserDataStore.laptops.remove(productId);
				return true;

			case "voiceassistant":

				SaxParserDataStore.voiceassistants.remove(productId);
				return true;
			case "accessory":

				SaxParserDataStore.accessories.remove(productId);
				return true;

		}
		return false;
	}

	public boolean isContainsStr(String string) {
        String regex = ".*[a-zA-Z]+.*";
        Matcher m = Pattern.compile(regex).matcher(string);
        return m.matches();
    }

	public boolean isItemExist(String itemCatalog, String itemName) {

        HashMap<String, Object> hm = new HashMap<String, Object>();

        switch (itemCatalog) {
            case "fitness":
                hm.putAll(SaxParserDataStore.fitnesswatches);
                break;
            case "smartwatch":
                hm.putAll(SaxParserDataStore.smartwatches);
                break;
            case "tv":
                hm.putAll(SaxParserDataStore.tvs);
                break;
            case "soundsystem":
                hm.putAll(SaxParserDataStore.soundsystems);
                break;
            case "headphone":
                hm.putAll(SaxParserDataStore.headphones);
                break;
            case "phone":
                hm.putAll(SaxParserDataStore.phones);
                break;
            case "laptop":
                hm.putAll(SaxParserDataStore.laptops);
                break;
            case "voiceassistant":
                hm.putAll(SaxParserDataStore.voiceassistants);
                break;
          case "accessory":
                hm.putAll(SaxParserDataStore.accessories);
              break;
        }
        return true;
	}
	
// 	public void removeOldOrder(int orderId, String orderName, String customerName) {
//         String TOMCAT_HOME = System.getProperty("catalina.home");
//         HashMap<Integer, ArrayList<OrderPayment>> orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();
//         ArrayList<OrderPayment> ListOrderPayment = new ArrayList<OrderPayment>();
//         //get the order from file
//         try {
//             FileInputStream fileInputStream = new FileInputStream(new File(TOMCAT_HOME + "\\webapps\\WebStore\\PaymentDetails.txt"));
//             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
//             orderPayments = (HashMap) objectInputStream.readObject();
//         } catch (Exception e) {

//         }
//         //get the exact order with same ordername and add it into cancel list to remove it later
//         for (OrderPayment oi : orderPayments.get(orderId)) {
//             if (oi.getOrderName().equals(orderName) && oi.getUserName().equals(customerName)) {
//                 ListOrderPayment.add(oi);
//                 //pw.print("<h4 style='color:red'>Your Order is Cancelled</h4>");
// //                        response.sendRedirect("SalesmanHome");
// //                        return;
//             }
//         }
//         //remove all the orders from hashmap that exist in cancel list
//         orderPayments.get(orderId).removeAll(ListOrderPayment);
//         if (orderPayments.get(orderId).size() == 0) {
//             orderPayments.remove(orderId);
//         }

//         //save the updated hashmap with removed order to the file
//         updateOrderFile(orderPayments);
// 	}
	public boolean removeOldOrder(int orderId, String orderName, String customerName) {
		return MySqlDataStoreUtilities.deleteOrder(orderId);
		
	}
	
	public boolean updateOrderFile(HashMap<Integer, ArrayList<OrderPayment>> orderPayments) {
        String TOMCAT_HOME = System.getProperty("catalina.home");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(TOMCAT_HOME + "\\webapps\\WebStore\\PaymentDetails.txt"));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(orderPayments);
            objectOutputStream.flush();
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {

        }
        return true;
	}

	public void updateOrder(int orderId, String customerName,
                            String orderName, double orderPrice, String userAddress, String creditCardNo) {
        MySqlDataStoreUtilities.deleteOrder(orderId);
        MySqlDataStoreUtilities.insertOrder(orderId, orderName, customerName, orderPrice, userAddress, creditCardNo);
    }

/* StoreProduct Function stores the Purchased product in Orders HashMap according to the User Names.*/
	public void storeProduct(String name,String type,String maker, String acc){
		if(!OrdersHashMap.orders.containsKey(username())){	
			ArrayList<OrderItem> arr = new ArrayList<OrderItem>();
			OrdersHashMap.orders.put(username(), arr);
		}

		ArrayList<OrderItem> orderItems = OrdersHashMap.orders.get(username());
		HashMap<String,Console> allconsoles = new HashMap<String,Console> ();
		HashMap<String,Laptop> alllaptops = new HashMap<String,Laptop> ();
		HashMap<String,Tv> alltvs = new HashMap<String,Tv> ();
		HashMap<String,SoundSystem> allsoundsystems = new HashMap<String,SoundSystem> ();
		HashMap<String,Phone> allphones = new HashMap<String,Phone> ();
		HashMap<String,VoiceAssistant> allvoiceassistants = new HashMap<String,VoiceAssistant> ();
		HashMap<String,FitnessWatch> allfitnesswatches = new HashMap<String,FitnessWatch> ();
		HashMap<String,SmartWatch> allsmartwatches = new HashMap<String,SmartWatch> ();
		HashMap<String,HeadPhone> allheadphones = new HashMap<String,HeadPhone> ();
		HashMap<String,WirelessPlan> allwirelessplans = new HashMap<String,WirelessPlan> ();
		HashMap<String,Accessory> allaccessories=new HashMap<String,Accessory>();
		if(type.equals("consoles")){
			Console console;
			try{
				//allconsoles = MySqlDataStoreUtilities.getConsoles();
			}
			catch(Exception e){
				
			}
			console = SaxParserDataStore.consoles.get(name);
			OrderItem orderitem = new OrderItem(console.getName(), console.getPrice(), console.getImage(), console.getRetailer());
			orderItems.add(orderitem);
		}
		
		
		if(type.equals("laptops")){
			System.out.println("Laptop");
			Laptop laptop;
			try{
				alllaptops = MySqlDataStoreUtilities.getLaptops();
			}
			catch(Exception e){
				
			}
			// laptop = SaxParserDataStore.laptops.get(name);
			laptop = alllaptops.get(name);
			OrderItem orderitem = new OrderItem(laptop.getName(), laptop.getPrice(), laptop.getImage(), laptop.getRetailer());
			orderItems.add(orderitem);
			
		}

		if(type.equals("phones")){
			Phone phone = null;
			try{
				allphones = MySqlDataStoreUtilities.getPhones();
			}
			catch(Exception e){
				
			}
			
			phone = allphones.get(name);
			OrderItem orderitem = new OrderItem(phone.getName(), phone.getPrice(), phone.getImage(), phone.getRetailer());
			orderItems.add(orderitem);
		}

		if(type.equals("headphones")){
			HeadPhone headphone = null;
			try{
				allheadphones = MySqlDataStoreUtilities.getHeadphones();
			}
			catch(Exception e){
				
			}
			headphone = allheadphones.get(name);
			OrderItem orderitem = new OrderItem(headphone.getName(), headphone.getPrice(), headphone.getImage(), headphone.getRetailer());
			orderItems.add(orderitem);
		}

		if(type.equals("fitnesswatches")){
			FitnessWatch fitnesswatch = null;
			try{
				allfitnesswatches = MySqlDataStoreUtilities.getFitnesswatches();
			}
			catch(Exception e){
				
			}
			fitnesswatch = allfitnesswatches.get(name);
			OrderItem orderitem = new OrderItem(fitnesswatch.getName(), fitnesswatch.getPrice(), fitnesswatch.getImage(), fitnesswatch.getRetailer());
			orderItems.add(orderitem);
		}

		if(type.equals("tvs")){
			Tv tv = null;
			try{
				alltvs = MySqlDataStoreUtilities.getTvs();
			}
			catch(Exception e){
				
			}
			tv = alltvs.get(name);
			OrderItem orderitem = new OrderItem(tv.getName(), tv.getPrice(), tv.getImage(), tv.getRetailer());
			orderItems.add(orderitem);
		}

		if(type.equals("soundsystems")){
			SoundSystem soundsystem = null;
			try{
				allsoundsystems = MySqlDataStoreUtilities.getSoundsystems();
			}
			catch(Exception e){
				
			}
			soundsystem = allsoundsystems.get(name);
			OrderItem orderitem = new OrderItem(soundsystem.getName(), soundsystem.getPrice(), soundsystem.getImage(), soundsystem.getRetailer());
			orderItems.add(orderitem);
		}

		if(type.equals("smartwatches")){
			SmartWatch smartwatch = null;
			try{
				allsmartwatches = MySqlDataStoreUtilities.getSmartwatches();
			}
			catch(Exception e){
				
			}
			smartwatch = allsmartwatches.get(name);
			OrderItem orderitem = new OrderItem(smartwatch.getName(), smartwatch.getPrice(), smartwatch.getImage(), smartwatch.getRetailer());
			orderItems.add(orderitem);
		}
	
		
		if(type.equals("voiceassistants")){
			VoiceAssistant voiceassistant = null;
			try{
				allvoiceassistants = MySqlDataStoreUtilities.getVoiceassistants();
			}
			catch(Exception e){
				
			}
			voiceassistant = allvoiceassistants.get(name);
			OrderItem orderitem = new OrderItem(voiceassistant.getName(), voiceassistant.getPrice(), voiceassistant.getImage(), voiceassistant.getRetailer());
			orderItems.add(orderitem);
		}

		if(type.equals("wirelessplan")){
			WirelessPlan wirelessplan = null;
			try{
				allwirelessplans = MySqlDataStoreUtilities.getWirelessplans();
			}
			catch(Exception e){
				
			}
			wirelessplan = allwirelessplans.get(name);
			OrderItem orderitem = new OrderItem(wirelessplan.getName(), wirelessplan.getPrice(), wirelessplan.getImage(), wirelessplan.getRetailer());
			orderItems.add(orderitem);
		}

		if(type.equals("games")){
			Game game = null;
			game = SaxParserDataStore.games.get(name);
			OrderItem orderitem = new OrderItem(game.getName(), game.getPrice(), game.getImage(), game.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("tablets")){
			Tablet tablet = null;
			tablet = SaxParserDataStore.tablets.get(name);
			OrderItem orderitem = new OrderItem(tablet.getName(), tablet.getPrice(), tablet.getImage(), tablet.getRetailer());
			orderItems.add(orderitem);
		}
		if(type.equals("accessories")){	
			try{
				allaccessories = MySqlDataStoreUtilities.getAccessories();
				}
				catch(Exception e){
					
				}
			Accessory accessory = allaccessories.get(name); 
			OrderItem orderitem = new OrderItem(accessory.getName(), accessory.getPrice(), accessory.getImage(), accessory.getRetailer());
			orderItems.add(orderitem);
		}
		
	}
	
	
	// store the payment details for orders
	public void storePayment(int orderId,
		String orderName,double orderPrice,String userAddress,String creditCardNo,String customer){
		HashMap<Integer, ArrayList<OrderPayment>> orderPayments= new HashMap<Integer, ArrayList<OrderPayment>>();
			// get the payment details file 
		try
		{
			orderPayments=MySqlDataStoreUtilities.selectOrder();
		}
		catch(Exception e)
		{
			
		}
		if(orderPayments==null)
		{
			orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();
		}
			// if there exist order id already add it into same list for order id or create a new record with order id
			
		if(!orderPayments.containsKey(orderId)){	
			ArrayList<OrderPayment> arr = new ArrayList<OrderPayment>();
			orderPayments.put(orderId, arr);
		}
		ArrayList<OrderPayment> listOrderPayment = orderPayments.get(orderId);		
		
		OrderPayment orderpayment = new OrderPayment(orderId,username(),orderName,orderPrice,userAddress,creditCardNo);
		listOrderPayment.add(orderpayment);	
			
			// add order details into database
		try
		{	if(session.getAttribute("usertype").equals("retailer"))
			{
				MySqlDataStoreUtilities.insertOrder(orderId,customer,orderName,orderPrice,userAddress,creditCardNo);
			}else
				
				{MySqlDataStoreUtilities.insertOrder(orderId,username(),orderName,orderPrice,userAddress,creditCardNo);}
		}
		catch(Exception e)
		{
			System.out.println("inside exception file not written properly");
		}	
	}
	public void storeNewOrder(int orderId,String customerName,String orderName, double orderPrice, String userAddress, String creditCardNo) {
        HashMap<Integer, ArrayList<OrderPayment>> orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();
        // String TOMCAT_HOME = System.getProperty("catalina.home");
        // get the payment details file
        try {
			MySqlDataStoreUtilities.insertOrder(orderId, customerName, orderName, orderPrice, userAddress, creditCardNo);
        } catch (Exception ignored) {

        }
        if (orderPayments == null) {
            orderPayments = new HashMap<Integer, ArrayList<OrderPayment>>();
        }
        // if there exist order id already add it into same list for order id or create a new record with order id

        if (!orderPayments.containsKey(orderId)) {
            ArrayList<OrderPayment> arr = new ArrayList<OrderPayment>();
            orderPayments.put(orderId, arr);
        }
        ArrayList<OrderPayment> listOrderPayment = orderPayments.get(orderId);

        OrderPayment orderpayment = new OrderPayment(orderId, customerName, orderName, orderPrice, userAddress, creditCardNo);
        listOrderPayment.add(orderpayment);

        // add order details into file
        updateOrderFile(orderPayments);

    }
	public String storeReview(String productname, String producttype, String productmaker, String reviewrating,
	                          String reviewdate, String reviewtext, String reatilerpin, String price, String city, String userAge, String userGender, String userOccupation){
		String message = MongoDBDataStoreUtilities.insertReview(productname, username(), producttype, productmaker, reviewrating, reviewdate, reviewtext, reatilerpin, price, city, userAge, userGender, userOccupation);
			if(!message.equals("Successfull"))
			{ return "UnSuccessfull";
			}
			else
			{
			HashMap<String, ArrayList<Review>> reviews= new HashMap<String, ArrayList<Review>>();
			try
			{
				reviews=MongoDBDataStoreUtilities.selectReview();
			}
			catch(Exception e)
			{
				
			}
			if(reviews==null)
			{
				reviews = new HashMap<String, ArrayList<Review>>();
			}
				// if there exist product review already add it into same list for productname or create a new record with product name
				
			if(!reviews.containsKey(productname)){	
				ArrayList<Review> arr = new ArrayList<Review>();
				reviews.put(productname, arr);
			}
			ArrayList<Review> listReview = reviews.get(productname);		
			Review review = new Review(productname,username(),producttype,productmaker,reviewrating,reviewdate,reviewtext,reatilerpin,price,city, userAge, userGender, userOccupation);
			listReview.add(review);	
				
				// add Reviews into database
			
			return "Successfull";	
			}
		}
		
	/* getConsoles Functions returns the Hashmap with all consoles in the store.*/

	public HashMap<String, Laptop> getLaptops(){
			HashMap<String, Laptop> hm = new HashMap<String, Laptop>();
			hm.putAll(SaxParserDataStore.laptops);
			return hm;
	}

	public HashMap<String, Phone> getPhones(){
		HashMap<String, Phone> hm = new HashMap<String, Phone>();
		hm.putAll(SaxParserDataStore.phones);
		return hm;
}
	public HashMap<String, HeadPhone> getHeadPhones(){
		HashMap<String, HeadPhone> hm = new HashMap<String, HeadPhone>();
		hm.putAll(SaxParserDataStore.headphones);
		return hm;
	}

	public HashMap<String, FitnessWatch> getFitnessWatches(){
		HashMap<String, FitnessWatch> hm = new HashMap<String,FitnessWatch>();
		hm.putAll(SaxParserDataStore.fitnesswatches);
		return hm;
	}
	
	public HashMap<String, Tv> getTvs(){
		HashMap<String, Tv> hm = new HashMap<String, Tv>();
		hm.putAll(SaxParserDataStore.tvs);
		return hm;
	}

	public HashMap<String, SoundSystem> getSoundSystems(){
		HashMap<String, SoundSystem> hm = new HashMap<String, SoundSystem>();
		hm.putAll(SaxParserDataStore.soundsystems);
		return hm;
	}
	public HashMap<String, SmartWatch> getSmartWatches(){
		HashMap<String, SmartWatch> hm = new HashMap<String, SmartWatch>();
		hm.putAll(SaxParserDataStore.smartwatches);
		return hm;
	}

	public HashMap<String, VoiceAssistant> getVoiceAssistants(){
		HashMap<String, VoiceAssistant> hm = new HashMap<String, VoiceAssistant>();
		hm.putAll(SaxParserDataStore.voiceassistants);
		return hm;
	}

	public HashMap<String, WirelessPlan> getWirelessPlans(){
		HashMap<String, WirelessPlan> hm = new HashMap<String, WirelessPlan>();
		hm.putAll(SaxParserDataStore.wirelessplans);
		return hm;
	}

	public HashMap<String, Accessory> getAccessories(){
		HashMap<String, Accessory> hm = new HashMap<String, Accessory>();
		hm.putAll(SaxParserDataStore.accessories);
		return hm;
	}
	/* getGames Functions returns the  Hashmap with all Games in the store.*/

	public HashMap<String, Game> getGames(){
			HashMap<String, Game> hm = new HashMap<String, Game>();
			hm.putAll(SaxParserDataStore.games);
			return hm;
	}
	
	/* getTablets Functions returns the Hashmap with all Tablet in the store.*/

	public HashMap<String, Tablet> getTablets(){
			HashMap<String, Tablet> hm = new HashMap<String, Tablet>();
			hm.putAll(SaxParserDataStore.tablets);
			return hm;
	}
	
	/* getProducts Functions returns the Arraylist of consoles in the store.*/

	public ArrayList<String> getProducts(){
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, Laptop> entry : getLaptops().entrySet()){			
			ar.add(entry.getValue().getName());
		}
		return ar;
	}
	
	/* getProducts Functions returns the Arraylist of games in the store.*/

	public ArrayList<String> getProductsGame(){		
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, Game> entry : getGames().entrySet()){
			ar.add(entry.getValue().getName());
		}
		return ar;
	}
	
	/* getProducts Functions returns the Arraylist of Tablets in the store.*/

	public ArrayList<String> getProductsTablets(){		
		ArrayList<String> ar = new ArrayList<String>();
		for(Map.Entry<String, Tablet> entry : getTablets().entrySet()){
			ar.add(entry.getValue().getName());
		}
		return ar;
	}
	
	

}
