package sernet.gs.server.commands;

/*******************************************************************************
 * Copyright (c) 2013 Sebastian Hagedorn <sh@sernet.de>.
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 *     This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public 
 * License along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Sebastian Hagedorn <sh@sernet.de> - initial API and implementation
 ******************************************************************************/

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jbpm.pvm.internal.task.TaskImpl;

import sernet.hui.common.VeriniceContext;
import sernet.verinice.bpm.TaskService;
import sernet.verinice.interfaces.GenericCommand;
import sernet.verinice.interfaces.bpm.ITaskService;

/**
 *
 */
public class DataSaveForWebservice extends GenericCommand {
    
	private long id;
	private String name;
	private JSONObject answer;
    private transient Logger log = Logger.getLogger(DataSaveForWebservice.class);

    public DataSaveForWebservice(String item){
    	
        try {
        	
        	//Instantiate JSONObject and get id and name
			answer = new JSONObject(item);
			id = Integer.parseInt(answer.get("id").toString());
			name = answer.get("name").toString();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
    }
    
    /* (non-Javadoc)
     * @see sernet.verinice.interfaces.ICommand#execute()
     */
    @Override
    public void execute() {
    	 	
        ITaskService iTaskService = (ITaskService)VeriniceContext.get(VeriniceContext.TASK_SERVICE);
        if(iTaskService instanceof TaskService){
            TaskService taskService = (TaskService)iTaskService;
            TaskImpl taskImpl =  taskService.getJbpmTaskDao().findById(id);
            taskImpl.setName(name);
            taskService.getJbpmTaskDao().saveOrUpdate(taskImpl);
        }
    }
    
    private Logger getLog(){
        if(log == null){
            log = Logger.getLogger(DataSaveForWebservice.class);
        }
        return log;
    }
}

