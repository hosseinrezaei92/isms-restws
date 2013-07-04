package sernet.verinice.jersey;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/json/auth")
public class Webservice_AuthCheck {
	
	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getAuthService(){
		
		System.out.println("Authentification accepted");
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject("{\"Access\":\"granted\"}");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObj;	
	}	
	
}