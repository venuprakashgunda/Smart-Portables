import java.sql.*;
import java.util.*;
                	
public class MySqlDataStoreUtilities
{
static Connection conn = null;
static String message;
public static String getConnection()
{

	try
	{
	Class.forName("com.mysql.jdbc.Driver").newInstance();
	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exampledatabase","root","root");							
	message="Successfull";
	return message;
}
	catch(SQLException e)
	{
		message="unsuccessful";
		     return message;
	}
	catch(Exception e)
	{
		message=e.getMessage();
		return message;
	}
}


public static boolean deleteOrder(int orderId)
{
	try
	{
		
		getConnection();
		String deleteOrderQuery ="Delete from customerorders where OrderId=?";
		PreparedStatement pst = conn.prepareStatement(deleteOrderQuery);
		pst.setInt(1,orderId);
		//pst.setString(2,orderName);
		pst.executeUpdate();
	}
	catch(Exception e)
	{
		System.out.println(e.getMessage());
            return false;	
    }
    return true;
}

public static void insertOrder(int orderId,String userName,String orderName,double orderPrice,String userAddress,String creditCardNo)
{
	try
	{
	
		getConnection();
		String insertIntoCustomerOrderQuery = "INSERT INTO customerOrders(OrderId,UserName,OrderName,OrderPrice,userAddress,creditCardNo) "
		+ "VALUES (?,?,?,?,?,?);";	
			
		PreparedStatement pst = conn.prepareStatement(insertIntoCustomerOrderQuery);
		//set the parameter for each column and execute the prepared statement
		pst.setInt(1,orderId);
		pst.setString(2,userName);
		pst.setString(3,orderName);
		pst.setDouble(4,orderPrice);
		pst.setString(5,userAddress);
		pst.setString(6,creditCardNo);
		pst.execute();
	}
	catch(Exception e)
	{
	
	}		
}

public static HashMap<Integer, ArrayList<OrderPayment>> selectOrder()
{	

	HashMap<Integer, ArrayList<OrderPayment>> orderPayments=new HashMap<Integer, ArrayList<OrderPayment>>();
		
	try
	{					

		getConnection();
        //select the table 
		String selectOrderQuery ="select * from customerorders";			
		PreparedStatement pst = conn.prepareStatement(selectOrderQuery);
		ResultSet rs = pst.executeQuery();	
		ArrayList<OrderPayment> orderList=new ArrayList<OrderPayment>();
		while(rs.next())
		{
			if(!orderPayments.containsKey(rs.getInt("OrderId")))
			{	
				ArrayList<OrderPayment> arr = new ArrayList<OrderPayment>();
				orderPayments.put(rs.getInt("orderId"), arr);
			}
			ArrayList<OrderPayment> listOrderPayment = orderPayments.get(rs.getInt("OrderId"));		
			System.out.println("data is"+rs.getInt("OrderId")+orderPayments.get(rs.getInt("OrderId")));

			//add to orderpayment hashmap
			OrderPayment order= new OrderPayment(rs.getInt("OrderId"),rs.getString("userName"),rs.getString("orderName"),rs.getDouble("orderPrice"),rs.getString("userAddress"),rs.getString("creditCardNo"));
			listOrderPayment.add(order);
					
		}
				
					
	}
	catch(Exception e)
	{
		
	}
	return orderPayments;
}


public static void insertUser(String username,String password,String repassword,String usertype)
{
	try
	{	

		getConnection();
		String insertIntoCustomerRegisterQuery = "INSERT INTO Registration(username,password,repassword,usertype) "
		+ "VALUES (?,?,?,?);";	
				
		PreparedStatement pst = conn.prepareStatement(insertIntoCustomerRegisterQuery);
		pst.setString(1,username);
		pst.setString(2,password);
		pst.setString(3,repassword);
		pst.setString(4,usertype);
		pst.execute();
	}
	catch(Exception e)
	{
	
	}	
}

public static HashMap<String,User> selectUser()
{	
	HashMap<String,User> hm=new HashMap<String,User>();
	try 
	{
		getConnection();
		Statement stmt=conn.createStatement();
		String selectCustomerQuery="select * from  Registration";
		ResultSet rs = stmt.executeQuery(selectCustomerQuery);
		while(rs.next())
		{	User user = new User(rs.getString("username"),rs.getString("password"),rs.getString("usertype"));
				hm.put(rs.getString("username"), user);
		}
	}
	catch(Exception e)
	{
	}
	return hm;			
}
//--------------------------------------------------------------------------------------------------------------------------------
    //Products
    public static void Insertproducts()
	{
	try{

		getConnection();

		String truncatetableacc = "delete from Product_accessories;";
		PreparedStatement pstt = conn.prepareStatement(truncatetableacc);
		pstt.executeUpdate();
		
		String truncatetableprod = "delete from  Productdetails;";
		PreparedStatement psttprod = conn.prepareStatement(truncatetableprod);
		psttprod.executeUpdate();

		String insertProductQurey = "INSERT INTO  Productdetails(ProductType,Id,productName,productPrice,productImage,productManufacturer,productCondition,productDiscount)" +
		"VALUES (?,?,?,?,?,?,?,?);";
        //Laptop
        for(Map.Entry<String,Laptop> entry : SaxParserDataStore.laptops.entrySet())
        {
			String name = "laptops";
            Laptop lap = entry.getValue();
			
        PreparedStatement pst = conn.prepareStatement(insertProductQurey);
        pst.setString(1,name);
        pst.setString(2,lap.getId());
        pst.setString(3,lap.getName());
        pst.setDouble(4,lap.getPrice());
        pst.setString(5,lap.getImage());
        pst.setString(6,lap.getRetailer());
        pst.setString(7,lap.getCondition());
        pst.setDouble(8,lap.getDiscount());

		pst.executeUpdate();
		try{
			HashMap<String,String> acc = lap.getAccessories();
			String insertAccessoryQurey = "INSERT INTO  Product_accessories(productName,accessoriesName)" +
			"VALUES (?,?);";
			for(Map.Entry<String,String> accentry : acc.entrySet())
			{
				PreparedStatement pstacc = conn.prepareStatement(insertAccessoryQurey);
				pstacc.setString(1,lap.getId());
				pstacc.setString(2,accentry.getValue());
				pstacc.executeUpdate();
			}
			}catch(Exception et){
				et.printStackTrace();
			}
        }

		//Tv
		for(Map.Entry<String,Tv> entry : SaxParserDataStore.tvs.entrySet())
		{   
			String name = "tvs";
	        Tv tv = entry.getValue();
			
			PreparedStatement pst = conn.prepareStatement(insertProductQurey);
			pst.setString(1,name);
			pst.setString(2,tv.getId());
			pst.setString(3,tv.getName());
			pst.setDouble(4,tv.getPrice());
			pst.setString(5,tv.getImage());
			pst.setString(6,tv.getRetailer());
			pst.setString(7,tv.getCondition());
			pst.setDouble(8,tv.getDiscount());
			
			pst.executeUpdate();	
		}

		//Sound System
		for(Map.Entry<String,SoundSystem> entry : SaxParserDataStore.soundsystems.entrySet())
		{   
			String name = "soundsystems";
	        SoundSystem soundsystem = entry.getValue();
			
			PreparedStatement pst = conn.prepareStatement(insertProductQurey);
			pst.setString(1,name);
			pst.setString(2,soundsystem.getId());
			pst.setString(3,soundsystem.getName());
			pst.setDouble(4,soundsystem.getPrice());
			pst.setString(5,soundsystem.getImage());
			pst.setString(6,soundsystem.getRetailer());
			pst.setString(7,soundsystem.getCondition());
			pst.setDouble(8,soundsystem.getDiscount());
			
			pst.executeUpdate();	
		}
		//Phone
		for(Map.Entry<String,Phone> entry : SaxParserDataStore.phones.entrySet())
		{   
			String name = "phones";
	        Phone phone = entry.getValue();
			
			PreparedStatement pst = conn.prepareStatement(insertProductQurey);
			pst.setString(1,name);
			pst.setString(2,phone.getId());
			pst.setString(3,phone.getName());
			pst.setDouble(4,phone.getPrice());
			pst.setString(5,phone.getImage());
			pst.setString(6,phone.getRetailer());
			pst.setString(7,phone.getCondition());
			pst.setDouble(8,phone.getDiscount());
			
			pst.executeUpdate();	
		}
		//Voice Assistant
		for(Map.Entry<String,VoiceAssistant> entry : SaxParserDataStore.voiceassistants.entrySet())
		{   
			String name = "voiceassistants";
	        VoiceAssistant voiceassistant = entry.getValue();
			
			PreparedStatement pst = conn.prepareStatement(insertProductQurey);
			pst.setString(1,name);
			pst.setString(2,voiceassistant.getId());
			pst.setString(3,voiceassistant.getName());
			pst.setDouble(4,voiceassistant.getPrice());
			pst.setString(5,voiceassistant.getImage());
			pst.setString(6,voiceassistant.getRetailer());
			pst.setString(7,voiceassistant.getCondition());
			pst.setDouble(8,voiceassistant.getDiscount());
			
			pst.executeUpdate();	
		}
		//Fitness Watch
		for(Map.Entry<String,FitnessWatch> entry : SaxParserDataStore.fitnesswatches.entrySet())
		{   
			String name = "fitnesswatches";
	        FitnessWatch fitnesswatch = entry.getValue();
			
			PreparedStatement pst = conn.prepareStatement(insertProductQurey);
			pst.setString(1,name);
			pst.setString(2,fitnesswatch.getId());
			pst.setString(3,fitnesswatch.getName());
			pst.setDouble(4,fitnesswatch.getPrice());
			pst.setString(5,fitnesswatch.getImage());
			pst.setString(6,fitnesswatch.getRetailer());
			pst.setString(7,fitnesswatch.getCondition());
			pst.setDouble(8,fitnesswatch.getDiscount());
			
			pst.executeUpdate();	
		}

		//Smart Watch
		for(Map.Entry<String,SmartWatch> entry : SaxParserDataStore.smartwatches.entrySet())
		{   
			String name = "smartwatches";
	        SmartWatch smartwatch = entry.getValue();
			
			PreparedStatement pst = conn.prepareStatement(insertProductQurey);
			pst.setString(1,name);
			pst.setString(2,smartwatch.getId());
			pst.setString(3,smartwatch.getName());
			pst.setDouble(4,smartwatch.getPrice());
			pst.setString(5,smartwatch.getImage());
			pst.setString(6,smartwatch.getRetailer());
			pst.setString(7,smartwatch.getCondition());
			pst.setDouble(8,smartwatch.getDiscount());
			
			pst.executeUpdate();	
		}
		//Head Phone
		for(Map.Entry<String,HeadPhone> entry : SaxParserDataStore.headphones.entrySet())
		{   
			String name = "headphones";
	        HeadPhone headphone = entry.getValue();
			
			PreparedStatement pst = conn.prepareStatement(insertProductQurey);
			pst.setString(1,name);
			pst.setString(2,headphone.getId());
			pst.setString(3,headphone.getName());
			pst.setDouble(4,headphone.getPrice());
			pst.setString(5,headphone.getImage());
			pst.setString(6,headphone.getRetailer());
			pst.setString(7,headphone.getCondition());
			pst.setDouble(8,headphone.getDiscount());
			
			pst.executeUpdate();	
		}
	//Wireless plan
	for(Map.Entry<String,WirelessPlan> entry : SaxParserDataStore.wirelessplans.entrySet())
	{   
		String name = "wirelessplans";
		WirelessPlan wirelessplan = entry.getValue();
		
		PreparedStatement pst = conn.prepareStatement(insertProductQurey);
		pst.setString(1,name);
		pst.setString(2,wirelessplan.getId());
		pst.setString(3,wirelessplan.getName());
		pst.setDouble(4,wirelessplan.getPrice());
		pst.setString(5,wirelessplan.getImage());
		pst.setString(6,wirelessplan.getRetailer());
		pst.setString(7,wirelessplan.getCondition());
		pst.setDouble(8,wirelessplan.getDiscount());
		
		pst.executeUpdate();	
	}


        }catch(Exception e)
        {
              e.printStackTrace();
		}
		
}
//Laptop
public static HashMap<String,Laptop> getLaptops()
{	
	HashMap<String,Laptop> hm=new HashMap<String,Laptop>();
	try 
	{
		getConnection();
		
		String selectLaptop="select * from  Productdetails where ProductType=?";
		PreparedStatement pst = conn.prepareStatement(selectLaptop);
		pst.setString(1,"laptops");
		ResultSet rs = pst.executeQuery();
	
		while(rs.next())
		{	Laptop lap = new Laptop(rs.getString("productName"),rs.getDouble("productPrice"),rs.getString("productImage"),rs.getString("productManufacturer"),rs.getString("productCondition"),rs.getDouble("productDiscount"));
				hm.put(rs.getString("Id"), lap);
				lap.setId(rs.getString("Id"));

		}
	}
	catch(Exception e)
	{
	}
	return hm;			
}
//Tv
public static HashMap<String,Tv> getTvs()
{	
	HashMap<String,Tv> hm=new HashMap<String,Tv>();
	try 
	{
		getConnection();
		
		String selectTv="select * from  Productdetails where ProductType=?";
		PreparedStatement pst = conn.prepareStatement(selectTv);
		pst.setString(1,"tvs");
		ResultSet rs = pst.executeQuery();
	
		while(rs.next())
		{	Tv tv = new Tv(rs.getString("productName"),rs.getDouble("productPrice"),rs.getString("productImage"),rs.getString("productManufacturer"),rs.getString("productCondition"),rs.getDouble("productDiscount"));
				hm.put(rs.getString("Id"), tv);
				tv.setId(rs.getString("Id"));

		}
	}
	catch(Exception e)
	{
	}
	return hm;			
}
//SoundSystem
public static HashMap<String,SoundSystem> getSoundsystems()
{	
	HashMap<String,SoundSystem> hm=new HashMap<String,SoundSystem>();
	try 
	{
		getConnection();
		
		String selectSoundSystem="select * from  Productdetails where ProductType=?";
		PreparedStatement pst = conn.prepareStatement(selectSoundSystem);
		pst.setString(1,"soundsystems");
		ResultSet rs = pst.executeQuery();
	
		while(rs.next())
		{	SoundSystem soundsystem = new SoundSystem(rs.getString("productName"),rs.getDouble("productPrice"),rs.getString("productImage"),rs.getString("productManufacturer"),rs.getString("productCondition"),rs.getDouble("productDiscount"));
				hm.put(rs.getString("Id"), soundsystem);
				soundsystem.setId(rs.getString("Id"));

		}
	}
	catch(Exception e)
	{
	}
	return hm;			
}
//Phone
public static HashMap<String,Phone> getPhones()
{	
	HashMap<String,Phone> hm=new HashMap<String,Phone>();
	try 
	{
		getConnection();
		
		String selectPhone="select * from  Productdetails where ProductType=?";
		PreparedStatement pst = conn.prepareStatement(selectPhone);
		pst.setString(1," phones");
		ResultSet rs = pst.executeQuery();
	
		while(rs.next())
		{	Phone phone = new Phone(rs.getString("productName"),rs.getDouble("productPrice"),rs.getString("productImage"),rs.getString("productManufacturer"),rs.getString("productCondition"),rs.getDouble("productDiscount"));
				hm.put(rs.getString("Id"), phone);
				phone.setId(rs.getString("Id"));

		}
	}
	catch(Exception e)
	{
	}
	return hm;			
}
//Voice Assistant
public static HashMap<String,VoiceAssistant> getVoiceassistants()
{	
	HashMap<String,VoiceAssistant> hm=new HashMap<String,VoiceAssistant>();
	try 
	{
		getConnection();
		
		String selectVoiceAssistant="select * from  Productdetails where ProductType=?";
		PreparedStatement pst = conn.prepareStatement(selectVoiceAssistant);
		pst.setString(1,"voiceassistants");
		ResultSet rs = pst.executeQuery();
	
		while(rs.next())
		{	VoiceAssistant voiceassistant = new VoiceAssistant(rs.getString("productName"),rs.getDouble("productPrice"),rs.getString("productImage"),rs.getString("productManufacturer"),rs.getString("productCondition"),rs.getDouble("productDiscount"));
				hm.put(rs.getString("Id"), voiceassistant);
				voiceassistant.setId(rs.getString("Id"));

		}
	}
	catch(Exception e)
	{
	}
	return hm;			
}
//Fitness Watch
public static HashMap<String,FitnessWatch> getFitnesswatches()
{	
	HashMap<String,FitnessWatch> hm=new HashMap<String,FitnessWatch>();
	try 
	{
		getConnection();
		
		String selectFitnesswatch="select * from  Productdetails where ProductType=?";
		PreparedStatement pst = conn.prepareStatement(selectFitnesswatch);
		pst.setString(1,"fitnesswatches");
		ResultSet rs = pst.executeQuery();
	
		while(rs.next())
		{	FitnessWatch fitnesswatch = new FitnessWatch(rs.getString("productName"),rs.getDouble("productPrice"),rs.getString("productImage"),rs.getString("productManufacturer"),rs.getString("productCondition"),rs.getDouble("productDiscount"));
				hm.put(rs.getString("Id"), fitnesswatch);
				fitnesswatch.setId(rs.getString("Id"));

		}
	}
	catch(Exception e)
	{
	}
	return hm;			
}
//Smart Watch
public static HashMap<String,SmartWatch> getSmartwatches()
{	
	HashMap<String,SmartWatch> hm=new HashMap<String,SmartWatch>();
	try 
	{
		getConnection();
		
		String selectSmartwatch="select * from  Productdetails where ProductType=?";
		PreparedStatement pst = conn.prepareStatement(selectSmartwatch);
		pst.setString(1,"smartwatches");
		ResultSet rs = pst.executeQuery();
	
		while(rs.next())
		{	SmartWatch smartwatch = new SmartWatch(rs.getString("productName"),rs.getDouble("productPrice"),rs.getString("productImage"),rs.getString("productManufacturer"),rs.getString("productCondition"),rs.getDouble("productDiscount"));
				hm.put(rs.getString("Id"), smartwatch);
				smartwatch.setId(rs.getString("Id"));

		}
	}
	catch(Exception e)
	{
	}
	return hm;			
}
//Head phones
public static HashMap<String,HeadPhone> getHeadphones()
{	
	HashMap<String,HeadPhone> hm=new HashMap<String,HeadPhone>();
	try 
	{
		getConnection();
		
		String selectHeadphone="select * from  Productdetails where ProductType=?";
		PreparedStatement pst = conn.prepareStatement(selectHeadphone);
		pst.setString(1,"headphones");
		ResultSet rs = pst.executeQuery();
	
		while(rs.next())
		{	HeadPhone headphone = new HeadPhone(rs.getString("productName"),rs.getDouble("productPrice"),rs.getString("productImage"),rs.getString("productManufacturer"),rs.getString("productCondition"),rs.getDouble("productDiscount"));
				hm.put(rs.getString("Id"), headphone);
				headphone.setId(rs.getString("Id"));

		}
	}
	catch(Exception e)
	{
	}
	return hm;			
}
//Head phones
public static HashMap<String,WirelessPlan> getWirelessplans()
{	
	HashMap<String,WirelessPlan> hm=new HashMap<String,WirelessPlan>();
	try 
	{
		getConnection();
		
		String selectWirelessplan ="select * from  Productdetails where ProductType=?";
		PreparedStatement pst = conn.prepareStatement(selectWirelessplan);
		pst.setString(1,"wirelessplans");
		ResultSet rs = pst.executeQuery(); 
	
		while(rs.next())
		{	WirelessPlan wirelessplan = new WirelessPlan(rs.getString("productName"),rs.getDouble("productPrice"),rs.getString("productImage"),rs.getString("productManufacturer"),rs.getString("productCondition"),rs.getDouble("productDiscount"));
				hm.put(rs.getString("Id"), wirelessplan);
				wirelessplan.setId(rs.getString("Id"));

		}
	}
	catch(Exception e)
	{
	}
	return hm;			
}
public static HashMap<String,Accessory> getAccessories()
{	
	HashMap<String,Accessory> hm=new HashMap<String,Accessory>();
	try 
	{
		getConnection();
		
		String selectAcc="select * from  Productdetails where ProductType=?";
		PreparedStatement pst = conn.prepareStatement(selectAcc);
		pst.setString(1,"accessories");
		ResultSet rs = pst.executeQuery();
	
		while(rs.next())
		{	Accessory acc = new Accessory(rs.getString("productName"),rs.getDouble("productPrice"),rs.getString("productImage"),rs.getString("productManufacturer"),rs.getString("productCondition"),rs.getDouble("productDiscount"));
				hm.put(rs.getString("Id"), acc);
				acc.setId(rs.getString("Id"));

		}
	}
	catch(Exception e)
	{
	}
	return hm;			
}
public static String addproducts(String producttype,String productId,String productName,double productPrice,String productImage,String productManufacturer,String productCondition,double productDiscount,String prod)
{
	String msg = "Product is added successfully";
	try{
		
		getConnection();
		String addProductQurey = "INSERT INTO  Productdetails(ProductType,Id,productName,productPrice,productImage,productManufacturer,productCondition,productDiscount)" +
		"VALUES (?,?,?,?,?,?,?,?);";
		   
			String name = producttype;
	        			
			PreparedStatement pst = conn.prepareStatement(addProductQurey);
			pst.setString(1,name);
			pst.setString(2,productId);
			pst.setString(3,productName);
			pst.setDouble(4,productPrice);
			pst.setString(5,productImage);
			pst.setString(6,productManufacturer);
			pst.setString(7,productCondition);
			pst.setDouble(8,productDiscount);
			
			pst.executeUpdate();
			try{
				if (!prod.isEmpty())
				{
					String addaprodacc =  "INSERT INTO  Product_accessories(productName,accessoriesName)" +
					"VALUES (?,?);";
					PreparedStatement pst1 = conn.prepareStatement(addaprodacc);
					pst1.setString(1,prod);
					pst1.setString(2,productId);
					pst1.executeUpdate();
					
				}
			}catch(Exception e)
			{
				msg = "Erro while adding the product";
				e.printStackTrace();
		
			}
			
			
		
	}
	catch(Exception e)
	{
		msg = "Erro while adding the product";
		e.printStackTrace();
		
	}
	return msg;
}
public static String updateproducts(String producttype,String productId,String productName,double productPrice,String productImage,String productManufacturer,String productCondition,double productDiscount)
{ 
    String msg = "Product is updated successfully";
	try{
		
		getConnection();
		String updateProductQurey = "UPDATE Productdetails SET productName=?,productPrice=?,productImage=?,productManufacturer=?,productCondition=?,productDiscount=? where Id =?;" ;
		
		   
				        			
			PreparedStatement pst = conn.prepareStatement(updateProductQurey);
			
			pst.setString(1,productName);
			pst.setDouble(2,productPrice);
			pst.setString(3,productImage);
			pst.setString(4,productManufacturer);
			pst.setString(5,productCondition);
			pst.setDouble(6,productDiscount);
			pst.setString(7,productId);
			pst.executeUpdate();
			
			
		
	}
	catch(Exception e)
	{
		msg = "Product cannot be updated";
		e.printStackTrace();
		
	}
 return msg;	
}
public static String deleteproducts(String productId)
{   String msg = "Product is deleted successfully";
	try
	{
		
		getConnection();
		String deleteproductsQuery ="Delete from Productdetails where Id=?";
		PreparedStatement pst = conn.prepareStatement(deleteproductsQuery);
		pst.setString(1,productId);
		
		pst.executeUpdate();
	}
	catch(Exception e)
	{
			msg = "Proudct cannot be deleted";
	}
	return msg;
}
public static void deleteOrder(int orderId,String orderName)
{
	try
	{
		
		getConnection();
		String deleteOrderQuery ="Delete from customerorders where OrderId=? and orderName=?";
		PreparedStatement pst = conn.prepareStatement(deleteOrderQuery);
		pst.setInt(1,orderId);
		pst.setString(2,orderName);
		pst.executeUpdate();
	}
	catch(Exception e)
	{
			
	}
}
}	