import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/HeadPhoneList")

public class HeadPhoneList extends HttpServlet {

	/* HeadPhone Page Displays all the HeadPhone and their Information in Best Deal */

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		HashMap<String,HeadPhone> allheadphones = new HashMap<String,HeadPhone> ();
		try{
			allheadphones = MySqlDataStoreUtilities.getHeadphones();
		}
		catch(Exception e)
		{
			
		}
		String name = null;
		String CategoryName = request.getParameter("maker");
        

		/* Checks the Phone type whether it is TV,Phone,Tablet etc */

		HashMap<String, HeadPhone> hm = new HashMap<String, HeadPhone>();
		if(CategoryName==null){
			hm.putAll(allheadphones);
			name = "";
		}
		else
		{
		   if(CategoryName.equals("sony"))
		   {
			 for(Map.Entry<String,HeadPhone> entry : SaxParserDataStore.headphones.entrySet())
			 {
				if(entry.getValue().getRetailer().equals("Sony"))
				 {
					 hm.put(entry.getValue().getId(),entry.getValue());
				 }
			 }
				name = "Sony";
		   }
		   else if(CategoryName.equals("jbl"))
		    {
			for(Map.Entry<String,HeadPhone> entry : SaxParserDataStore.headphones.entrySet())
				{
				 if(entry.getValue().getRetailer().equals("JBL"))
				 {
					 hm.put(entry.getValue().getId(),entry.getValue());
				 }
				}
				 name = "JBL";
			}
		}

		
		/* Header, Left Navigation Bar are Printed.

		All the Product and Product information are dispalyed in the Product Section

		and then Footer is Printed*/

		Utilities utility = new Utilities(request,pw);
		utility.printHtml("Header.html");
		utility.printHtml("LeftNavigationBar.html");
		pw.print("<div id='content'><div class='post'><h2 class='title meta'>");
		pw.print("<a style='font-size: 24px;'>"+name+" HeadPhones</a>");
		pw.print("</h2><div class='entry'><table id='bestseller'>");
		int i = 1; int size= hm.size();
		for(Map.Entry<String, HeadPhone> entry : hm.entrySet())
		{
			HeadPhone headphone = entry.getValue();
			if(i%3==1) pw.print("<tr>");
			pw.print("<td><div id='shop_item'>");
			pw.print("<h3>"+headphone.getName()+"</h3>");
			pw.print("<strong>$"+headphone.getPrice()+"</strong><ul>");
			pw.print("<li id='item'><img src='images/headphones/"+headphone.getImage()+"' alt='' /></li>");
			
			pw.print("<li><form method='post' action='Cart'>" +
					"<input type='hidden' name='name' value='"+entry.getKey()+"'>"+
					"<input type='hidden' name='type' value='headphones'>"+
					"<input type='hidden' name='maker' value='"+CategoryName+"'>"+
					"<input type='hidden' name='access' value=''>"+
					"<input type='submit' class='btnbuy' value='Buy Now'></form></li>");
			pw.print("<li><form method='post' action='WriteReview'>"+"<input type='hidden' name='name' value='"+entry.getKey()+"'>"+
					"<input type='hidden' name='type' value='headphones'>"+
					"<input type='hidden' name='maker' value='"+headphone.getRetailer()+"'>"+
					"<input type='hidden' name='price' value='"+headphone.getPrice()+"'>"+
					"<input type='hidden' name='access' value=''>"+
				    "<input type='submit' value='WriteReview' class='btnreview'></form></li>");
			pw.print("<li><form method='post' action='ViewReview'>"+"<input type='hidden' name='name' value='"+entry.getKey()+"'>"+
					"<input type='hidden' name='type' value='headphones'>"+
					// "<input type='hidden' name='maker' value='"+CategoryName+"'>"+
					"<input type='hidden' name='access' value=''>"+
				    "<input type='submit' value='ViewReview' class='btnreview'></form></li>");
			pw.print("</ul></div></td>");
			if(i%3==0 || i == size) pw.print("</tr>");
			i++;
		}	
		pw.print("</table></div></div></div>");
   
		utility.printHtml("Footer.html");
		
	}
}
