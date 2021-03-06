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
package sernet.gs.ui.rcp.main.bsi.model;

import java.io.Serializable;

import sernet.verinice.model.common.CnATreeElement;

public class DocumentReference implements Serializable {

	private CnATreeElement cnatreeelement;
	private DocumentLink parent;

	public DocumentReference(CnATreeElement element) {
		cnatreeelement = element;
	}

	public CnATreeElement getCnaTreeElement() {
		return this.cnatreeelement;
	}
	
	public void setParent(DocumentLink parent) {
		this.parent = parent;
	}

	public DocumentLink getParent() {
		return parent;
	}

}
