package sernet.gs.ui.rcp.main.bsi.wizards;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;

import sernet.gs.ui.rcp.main.Activator;
import sernet.gs.ui.rcp.main.ExceptionUtil;
import sernet.gs.ui.rcp.main.common.model.CnAElementFactory;
import sernet.gs.ui.rcp.main.preferences.PreferenceConstants;
import sernet.gs.ui.rcp.main.service.ServiceFactory;
import sernet.verinice.interfaces.CommandException;
import sernet.verinice.model.common.CnATreeElement;
import sernet.verinice.service.commands.SyncCommand;
import sernet.verinice.service.commands.SyncParameter;
import de.sernet.sync.data.SyncAttribute;
import de.sernet.sync.data.SyncData;
import de.sernet.sync.data.SyncObject;
import de.sernet.sync.mapping.SyncMapping;
import de.sernet.sync.mapping.SyncMapping.MapObjectType.MapAttributeType;
import de.sernet.sync.sync.SyncRequest;

public class ImportCSVWizard extends Wizard {
    private static final Logger LOG = Logger.getLogger(ImportCSVWizard.class);
    
	private EntitySelectionPage entityPage;
	private PropertiesSelectionPage propertyPage;

    private String sourceId;

    public ImportCSVWizard() {
		super();
		propertyPage = new PropertiesSelectionPage(Messages.EntitySelectionPage_0);
		entityPage = new EntitySelectionPage(Messages.PropertiesSelectionPage_0);
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
    public boolean performFinish() {
		Activator.inheritVeriniceContextState();

		SyncRequest sr = null;
        try {
            sr = createSyncRequest();
        } catch (Exception e) {
            LOG.error("Error while creating sync request.", e); //$NON-NLS-1$
            ExceptionUtil.log(e, Messages.ImportCSVWizard_1);
        }

		
		SyncCommand command = new SyncCommand(
                new SyncParameter(
                        entityPage.getInsertState(), 
                        entityPage.getUpdateState(), 
                        entityPage.getDeleteState(),
                        false,
                        SyncParameter.EXPORT_FORMAT_XML_PURE), 
                sr);
		try {
			command = ServiceFactory.lookupCommandService().executeCommand(command);
		} catch (CommandException e) {
			throw new IllegalStateException(e);
		}
		
		Set<CnATreeElement> importRootObjectSet = command.getImportRootObject();
        Set<CnATreeElement> changedElement = command.getElementSet();
        updateModel(importRootObjectSet, changedElement);
        if(Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.USE_AUTOMATIC_VALIDATION)){
            createValidations(changedElement);
        }
		return true;
	}
	
	private void updateModel(Set<CnATreeElement> importRootObjectSet, Set<CnATreeElement> changedElement) {   
    	if (importRootObjectSet != null && importRootObjectSet.size()>0) {     
        	for (CnATreeElement importRootObject : importRootObjectSet) {        
                CnAElementFactory.getModel(importRootObject).childAdded(importRootObject.getParent(), importRootObject);
                CnAElementFactory.getModel(importRootObject).databaseChildAdded(importRootObject);
                if (changedElement != null) {
                    for (CnATreeElement cnATreeElement : changedElement) {
                        CnAElementFactory.getModel(cnATreeElement).childAdded(cnATreeElement.getParent(), cnATreeElement);
                        CnAElementFactory.getModel(cnATreeElement).databaseChildAdded(cnATreeElement);
                    }
                }
        	}
        } else {    
            if (changedElement != null) {
                for (CnATreeElement cnATreeElement : changedElement) {
                    CnAElementFactory.getModel(cnATreeElement).childChanged(cnATreeElement);
                    CnAElementFactory.getModel(cnATreeElement).databaseChildChanged(cnATreeElement);
                }
            }
        }       
    }

	@Override
    public void addPages() {
		addPage(entityPage);
		addPage(propertyPage);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
    public IWizardPage getNextPage(IWizardPage page) {
		if (page == entityPage) {
		    try {
    		    propertyPage.setEntityName(entityPage.getEntityName());
    		    propertyPage.setEntityId(entityPage.getEntityNameId());             
    		    propertyPage.setCSVDatei(entityPage.getCSVDatei());
    			setSourceId(entityPage.getSourceIdText().getText());
    			propertyPage.setSeparator(entityPage.getSeparatorCombo().getText().charAt(0));
    			propertyPage.setCharset(Charset.forName(entityPage.getCharsetCombo().getText()));
    			String[] firstLine = propertyPage.getFirstLine();
    			if(firstLine==null || firstLine.length==0) {
    			    final String message = NLS.bind(Messages.ImportCSVWizard_2, entityPage.getCSVDatei().getPath());
    			    LOG.warn(message);
    			    MessageDialog.openWarning(this.getShell(), Messages.ImportCSVWizard_3, message);
    			} else {
    			    if(firstLine[0]==null || !firstLine[0].toLowerCase().contains("ext")) { //$NON-NLS-1$
    			        final String message = Messages.ImportCSVWizard_5;
                        LOG.warn(message + " File: " + entityPage.getCSVDatei().getPath()); //$NON-NLS-1$
                        entityPage.setWarning(message);
    			    } else {
    			        entityPage.setWarning(""); //$NON-NLS-1$
    			    }
    			}
                propertyPage.fillTable();
            } catch (Exception e) {
                LOG.error("Error while creating next wizard page.", e); //$NON-NLS-1$
            }
		}
		return super.getNextPage(page);
	}

	/**
	 * Creates {@link SyncRequest} instance out of the values and settings given in
	 * <code>propertyPage</code>.
	 * 
	 * @return
	 * @throws IOException 
	 */
	private SyncRequest createSyncRequest() throws IOException {
		String entityName = propertyPage.getEntityName();

		SyncRequest sr = new SyncRequest();
		sr.setSourceId(getSourceId());
		
		// Prepare mapping
		SyncMapping sm = new SyncMapping();
		sr.setSyncMapping(sm);

		List<SyncMapping.MapObjectType> mots = sm.getMapObjectType();
		SyncMapping.MapObjectType mot = new SyncMapping.MapObjectType();
		mot.setExtId(entityName);
		mot.setIntId(entityPage.getEntityNameId());
		
		// list of properties user has mapped
		List<String> extIdList = new LinkedList<String>();
		for (List<String> properties : propertyPage.getPropertyTable()) {
            String intId = properties.get(0);
		    String extId = properties.get(1);
		    if(isValid(intId,extId)) {
    			MapAttributeType mat = new MapAttributeType();
    			mat.setExtId(extId);
    			extIdList.add(extId);
    			mat.setIntId(intId);
    			mot.getMapAttributeType().add(mat);
		    }
		}
		mots.add(mot);

		// Prepare data
		SyncData sd = new SyncData();
		sr.setSyncData(sd);
		String[] columns = propertyPage.getFirstLine();
		int i = 0;
		List<SyncObject> syncObjects = sd.getSyncObject();
		for (List<String> tableLine : propertyPage.getContent()) {
			SyncObject so = new SyncObject();		
			so.setExtObjectType(entityName);
			List<SyncAttribute> syncAttributes = so.getSyncAttribute();
			int j = 0;
			for (String tableCell : tableLine) {
			    if(j==0) {
			        so.setExtId(tableCell);
			    } else if(extIdList.contains(columns[j])) { /* only import columns the user has mapped */
    				SyncAttribute sa = new SyncAttribute();
    				sa.setName(columns[j]);
    				
    				// TODO: Table element should actually be a list of values.
    				// With this approach the import will be fine but the CSV format
    				// will not have the ability to properly represent verinice's
    				// way of handling multiple property values (which are lists internally).
    				sa.getValue().add(tableCell);
    
    				syncAttributes.add(sa);
			    }
				j++;
			}
			syncObjects.add(so);
			i++;
		}
		return sr;
	}

    private boolean isValid(String intId, String extId) {       
        return intId!=null && extId!=null && !intId.trim().isEmpty() && !extId.trim().isEmpty();
    }


    /**
     * @return
     */
    public String getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    
    private void createValidations(Set<CnATreeElement> elmts){
        for(CnATreeElement elmt : elmts){
            ServiceFactory.lookupValidationService().createValidationForSingleElement(elmt);
        }
        if(elmts != null && elmts.size() > 0){
            CnAElementFactory.getModel(((CnATreeElement)elmts.toArray()[0])).validationAdded(((CnATreeElement)elmts.toArray()[0]).getScopeId()); 
        }
    }
}
