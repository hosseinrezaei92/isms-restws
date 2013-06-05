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
import sernet.verinice.model.bsi.MassnahmenUmsetzung;
import sernet.verinice.model.common.CnATreeElement;

@Path("/json/metallica")
public class Connection {
 
	private ICommandService commandService;
	private Set<UnresolvedItem> unresolvedItems = new HashSet<UnresolvedItem>();
	private Set<TodoViewItem> massnahmen = new HashSet<TodoViewItem>(20);
	
	Set<TodoViewItem> cna;
	
	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Track getTrackInJSON() {
 
		Track track = new Track();
		track.setTitle("MyName");
		track.setSinger("Metallica");
 
		return track;
 
	}
 
	@GET
	@Path("/todo")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<TodoViewItem> getService() {
	
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