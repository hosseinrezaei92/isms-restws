/*******************************************************************************
 * Copyright (c) 2009 Alexander Koderman <ak[at]sernet[dot]de>.
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 *     This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 *     You should have received a copy of the GNU Lesser General Public 
 * License along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Alexander Koderman <ak[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.gs.ui.rcp.main.service.taskcommands.riskanalysis;

import sernet.verinice.interfaces.GenericCommand;
import sernet.verinice.model.bsi.risikoanalyse.FinishedRiskAnalysisLists;
import sernet.verinice.model.bsi.risikoanalyse.GefaehrdungsUmsetzung;
import sernet.verinice.model.bsi.risikoanalyse.GefaehrdungsUtil;
import sernet.verinice.model.common.CnATreeElement;
import sernet.verinice.model.common.HydratorUtil;

/**
 * Mark a threat as "OK" which menas it does not need further evaluation
 * during risk analysis.
 * 
 * @author koderman[at]sernet[dot]de
 * @version $Rev$ $LastChangedDate$ 
 * $LastChangedBy$
 *
 */
public class PositiveEstimateGefaehrdung extends GenericCommand {

	private Integer listsDbId;
	private GefaehrdungsUmsetzung gefaehrdungsUmsetzung;
	private CnATreeElement finishedRiskAnalysis;
	private FinishedRiskAnalysisLists finishedRiskLists;

	/**
	 * @param dbId
	 * @param gefaehrdungsUmsetzung
	 * @param finishedRiskAnalysis
	 */
	public PositiveEstimateGefaehrdung(Integer dbId,
			GefaehrdungsUmsetzung gefaehrdungsUmsetzung,
			CnATreeElement finishedRiskAnalysis) {
		this.finishedRiskAnalysis = finishedRiskAnalysis;
		this.gefaehrdungsUmsetzung = gefaehrdungsUmsetzung;
		this.listsDbId = dbId;
	}

	/* (non-Javadoc)
	 * @see sernet.gs.ui.rcp.main.service.commands.ICommand#execute()
	 */
	public void execute() {
		// attach objects:
		finishedRiskLists = getDaoFactory().getDAO(FinishedRiskAnalysisLists.class)
			.findById(listsDbId);
	
		getDaoFactory().getDAOforTypedElement(finishedRiskAnalysis)
			.reload(finishedRiskAnalysis, finishedRiskAnalysis.getDbId());
		
		gefaehrdungsUmsetzung = (GefaehrdungsUmsetzung) getDaoFactory().getDAOforTypedElement(gefaehrdungsUmsetzung)
			.merge(gefaehrdungsUmsetzung);
		
		gefaehrdungsUmsetzung.setOkay(true);
		finishedRiskAnalysis.removeChild(gefaehrdungsUmsetzung);

		GefaehrdungsUtil.removeBySameId(finishedRiskLists
				.getAllGefaehrdungsUmsetzungen(), gefaehrdungsUmsetzung);
		
		GefaehrdungsUtil.removeBySameId(finishedRiskLists
				.getNotOKGefaehrdungsUmsetzungen(), gefaehrdungsUmsetzung);
		
		
	}

	public GefaehrdungsUmsetzung getGefaehrdungsUmsetzung() {
		return gefaehrdungsUmsetzung;
	}

	/* (non-Javadoc)
	 * @see sernet.gs.ui.rcp.main.service.commands.GenericCommand#clear()
	 */
	@Override
	public void clear() {
		finishedRiskAnalysis = null;
		HydratorUtil.hydrateElement(getDaoFactory().getDAO(FinishedRiskAnalysisLists.class), finishedRiskLists);
	
	}

	public FinishedRiskAnalysisLists getLists() {
		return finishedRiskLists;
	}
}
