package sernet.verinice.jersey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jbpm.pvm.internal.task.TaskImpl;
 
import sernet.gs.server.commands.DataSaveForWebservice;
import sernet.gs.ui.rcp.main.bsi.model.TodoViewItem;
import sernet.gs.ui.rcp.main.service.taskcommands.LoadChildrenAndMassnahmen;
import sernet.hui.common.VeriniceContext;
import sernet.verinice.bpm.TaskService;
import sernet.verinice.interfaces.CommandException;
import sernet.verinice.interfaces.ICommandService;
import sernet.verinice.interfaces.bpm.ITask;
import sernet.verinice.interfaces.bpm.ITaskParameter;
import sernet.verinice.interfaces.bpm.ITaskService;
import sernet.verinice.iso27k.service.Retriever;
import sernet.verinice.model.bpm.TaskInformation;
import sernet.verinice.model.bpm.TaskParameter;

@Path("/json")
public class Webservice{
 
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
	public List<ITask> getIso27000(){
		
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
	public JSONObject saveIso27000Data(String item) {
		String response = "";
		System.out.println("ISO_27000_REST_Webservice saving service called!");
		System.out.println("Übergebenes Item: " + item);
		

	        DataSaveForWebservice command = new DataSaveForWebservice(item);
	        
	        try {
	        	
				this.getCommandService().executeCommand(command);
				
			} catch (CommandException e) {
				e.printStackTrace();
			}
	        
	       
		
		return createAnswer(response);
		
	}
	
	@GET
	@Path("/bsi_controls/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<TodoViewItem> getBsiControls() {
	
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
	public JSONObject saveBsiControlsData(TodoViewItem item) {
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
	
	private TaskService getTaskService() {
        return (TaskService) VeriniceContext.get(VeriniceContext.TASK_SERVICE);
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