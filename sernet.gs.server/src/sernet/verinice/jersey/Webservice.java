package sernet.verinice.jersey;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
 
import sernet.gs.server.security.DummyAuthentication;
import sernet.gs.server.security.InternalAuthenticationProvider;
import sernet.gs.service.ServerInitializer;
import sernet.gs.ui.rcp.main.bsi.model.TodoViewItem;
import sernet.gs.ui.rcp.main.service.taskcommands.FindMassnahmeById;
import sernet.gs.ui.rcp.main.service.taskcommands.FindResponsiblePersons;
import sernet.gs.ui.rcp.main.service.taskcommands.LoadChildrenAndMassnahmen;
import sernet.gs.ui.rcp.main.service.taskcommands.UnresolvedItem;
import sernet.gs.web.*;
import sernet.hui.common.VeriniceContext;
import sernet.verinice.interfaces.CommandException;
import sernet.verinice.interfaces.ICommand;
import sernet.verinice.interfaces.ICommandService;
import sernet.verinice.interfaces.bpm.ITask;
import sernet.verinice.interfaces.bpm.ITaskParameter;
import sernet.verinice.interfaces.bpm.ITaskService;
import sernet.verinice.model.bpm.TaskInformation;
import sernet.verinice.model.bpm.TaskParameter;
import sernet.verinice.model.bsi.MassnahmenUmsetzung;
import sernet.verinice.model.common.CnATreeElement;

@Path("/json")
public class Webservice {
 
	private List<ITask> taskList;
	Set<TodoViewItem> cna;
	private ICommandService commandService;
	
	@GET
	@Path("/auth/get")
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
	
	
	@GET
	@Path("/iso_27000/get")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ITask> getISO27000(){
		
		System.out.println("ISO_REST_Webservice called!");
		
		ITaskParameter parameter = new TaskParameter();
        parameter.setRead(true);
        parameter.setUnread(true);  
        
        taskList = getTaskService().getTaskList(parameter);

		return taskList;	
	}
	
	@POST
	@Path("/iso_27000/post")
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject saveISO27000Data(String item) {
		String answer = "";
		System.out.println("ISO_27000_REST_Webservice saving service called!");
		System.out.println("Übergebenes Item: " + item);

		return createAnswer(answer);
		
	}
	
	@GET
	@Path("/bsi_controls/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<TodoViewItem> getBSIControls() {
	
		System.out.println("BSI_REST_Webservice called!");
		
		LoadChildrenAndMassnahmen command = new LoadChildrenAndMassnahmen(88);
	
		Set<TodoViewItem> all = null;
		try {
			command = this.getCommandService().executeCommand(command);
			System.out.println("Command: " + command);
			System.out.println("Aufgerufen command "  + command);
			all = command.getMassnahmen();
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Aufgerufen");
 
		return all;
 
	}
	
	@POST
	@Path("/bsi_controls/post")
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject saveBSIControlsData(TodoViewItem item) {
		String answer = "";
		System.out.println("BSI_REST_Webservice saving service called!");
		System.out.println(item);
		
		
		
		return createAnswer(answer);
		
	}
	
	private JSONObject createAnswer(String answer) {
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject("{\"Saving\":\"" + answer +"\"}");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObj;
	}
	
	private ITaskService getTaskService() {
        return (ITaskService) VeriniceContext.get(VeriniceContext.TASK_SERVICE);
    }
	
	private ICommandService getCommandService() {
        if(commandService==null) {
            commandService = createCommandService();
        }
        return commandService;
    }
    
    private ICommandService createCommandService() {
        return(ICommandService) VeriniceContext.get(VeriniceContext.COMMAND_SERVICE);
    }
}