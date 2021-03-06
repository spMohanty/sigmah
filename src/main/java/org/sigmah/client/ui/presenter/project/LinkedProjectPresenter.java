package org.sigmah.client.ui.presenter.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.project.LinkedProjectView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;
import org.sigmah.client.util.NumberUtils;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.ProjectFundingDTO.LinkedProjectType;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.sigmah.shared.util.ValueResultUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Linked project (funding/funded) presenter which manages the {@link LinkedProjectView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class LinkedProjectPresenter extends AbstractPagePresenter<LinkedProjectPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(LinkedProjectView.class)
	public static interface View extends ViewInterface {

		/**
		 * Sets the initialization mode.
		 * 
		 * @param projectType
		 *          The linked project type.
		 * @param selection
		 *          {@code true} if the view is initialized for selection, {@code false} for modification.
		 * @param projectName
		 *          The parent project name.
		 */
		void setInitializationMode(LinkedProjectType projectType, boolean selection, final String projectName);

		FormPanel getForm();

		ComboBox<ModelData> getProjectsField();

		LabelField getProjectTypeField();

		NumberField getAmountField();

		LabelField getPercentageField();

		Button getSaveButton();

		Button getDeleteButton();

		void setProjectType(final ProjectModelType type);

	}

	/**
	 * A comparator which sorts the {@link ProjectDTO} by their names.
	 */
	private static final Comparator<ProjectDTO> PROJECT_NAME_COMPARATOR = new Comparator<ProjectDTO>() {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final ProjectDTO project1, final ProjectDTO project2) {

			if (project1 == null) {
				return project2 == null ? 0 : -1;
			}

			if (project2 == null) {
				return 1;
			}

			return project1.getName() != null ? project1.getName().compareToIgnoreCase(project2.getName()) : -1;
		}
	};

	/**
	 * A comparator which sorts the {@link CountryDTO} by their names.
	 */
	private static final Comparator<CountryDTO> COUNTRY_NAME_COMPARATOR = new Comparator<CountryDTO>() {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final CountryDTO country1, final CountryDTO country2) {

			if (country1 == null) {
				return country2 == null ? 0 : -1;
			}

			if (country2 == null) {
				return 1;
			}

			return country1.getName() != null ? country1.getName().compareToIgnoreCase(country2.getName()) : -1;
		}
	};

	/**
	 * Linked project type.
	 */
	private LinkedProjectType projectType;

	/**
	 * Parent project provided. Should never be {@code null}.
	 */
	private ProjectDTO parentProject;

	/**
	 * Linked project provided for edition mode.<br>
	 * For selection, this attribute is {@code null}.
	 */
	private ProjectFundingDTO linkedProject;

	/**
	 * The parent project planned budget.<br>
	 * May be {@code null} if not properly initialized.
	 */
	private Double plannedBudget;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public LinkedProjectPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.LINKED_PROJECT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Projects store listener.
		// --

		view.getProjectsField().getStore().addStoreListener(new StoreListener<ModelData>() {

			@Override
			public void storeClear(final StoreEvent<ModelData> se) {
				view.getProjectsField().setEnabled(false);
			}

			@Override
			public void storeAdd(final StoreEvent<ModelData> se) {
				view.getProjectsField().setEnabled(true);
			}

		});

		// --
		// Add a listener for the event fired when the amountField's value is changed.
		// --

		view.getAmountField().addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent event) {
				updatePercentageField();
			}
		});

		// --
		// Projects list selection change listener.
		// --

		view.getProjectsField().addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(final SelectionChangedEvent<ModelData> be) {

				final List<ModelData> selection = view.getProjectsField().getSelection();
				if (ClientUtils.isEmpty(selection) || !(selection.get(0) instanceof ProjectDTO)) {
					view.getProjectsField().clearSelections();
					return;
				}

				final ProjectDTO selectedProject = (ProjectDTO) selection.get(0);

				view.setProjectType(selectedProject.getProjectModelType(auth().getOrganizationId()));
			}
		});

		// --
		// Save button handler.
		// --

		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				onSaveAction();
			}
		});

		// --
		// Delete button handler.
		// --

		view.getDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				onDeleteAction();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		view.getForm().clear();
		view.getPercentageField().setValue(0 + " %");

		// --
		// Retrieving linked project type (funding/funded).
		// --

		projectType = LinkedProjectType.fromString(request.getParameter(RequestParameter.TYPE));
		if (projectType == null) {
			hideView();
			throw new IllegalArgumentException("Invalid linked project type (funding or funded).");
		}

		// --
		// Retrieving parent project.
		// --

		parentProject = request.getData(RequestParameter.HEADER);
		if (parentProject == null) {
			hideView();
			throw new IllegalArgumentException("Invalid parent project data.");
		}

		findPlannedBudget(parentProject);

		// --
		// Retrieving edited linked project (not present for selection).
		// --

		linkedProject = request.getData(RequestParameter.DTO);
		final boolean selection = linkedProject == null;

		// --
		// Prepares view.
		// --

		switch (projectType) {
			case FUNDING_PROJECT:
				setPageTitle(I18N.CONSTANTS.createProjectTypeFunding());
				break;

			case FUNDED_PROJECT:
				setPageTitle(I18N.CONSTANTS.createProjectTypePartner());
				break;

			default:
				break;
		}

		view.setInitializationMode(projectType, selection, parentProject.getName());

		if (selection) {
			loadProjects();
		}
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Updates the percentage field value.
	 */
	private void updatePercentageField() {

		if (view.getAmountField().getValue() == null) {
			view.getAmountField().setValue(0);
		}

		view.getPercentageField().setValue(NumberUtils.ratioAsString(view.getAmountField().getValue(), plannedBudget));
	}

	/**
	 * Retrieves the given {@code parentProject} corresponding planned budget value.
	 * 
	 * @param parentProject
	 *          The parent project.
	 */
	private void findPlannedBudget(final ProjectDTO parentProject) {

		plannedBudget = null;
		BudgetElementDTO budgetElementDTO = null;

		// Retrieves budget element.
		for (final FlexibleElementDTO flexibleElement : parentProject.getProjectModel().getGlobalExportElements()) {
			if (flexibleElement instanceof BudgetElementDTO) {
				budgetElementDTO = (BudgetElementDTO) flexibleElement;
			}
		}

		if (budgetElementDTO == null) {
			// TODO What should we do when no budget element has been found into parent project ?
			throw new UnsupportedOperationException("No budget element has been found into parent project.");
		}

		final BudgetSubFieldDTO plannedBudgetField = budgetElementDTO.getPlannedBudget();

		// Retrieves the budget element corresponding value.
		dispatch.execute(new GetValue(parentProject.getId(), budgetElementDTO.getId(), budgetElementDTO.getEntityName()), new CommandResultHandler<ValueResult>() {

			@Override
			public void onCommandSuccess(final ValueResult result) {

				final Map<Integer, String> values = ValueResultUtils.splitMapElements(result.getValueObject());

				if (values.containsKey(plannedBudgetField.getId())) {
					plannedBudget = Double.valueOf(values.get(plannedBudgetField.getId()));
				}

				if (linkedProject != null) {
					view.getAmountField().setValue(linkedProject.getPercentage());
					updatePercentageField();
				}
			}
		});
	}

	/**
	 * Retrieves the projects and populates the corresponding field.
	 */
	private void loadProjects() {

		final GetProjects command = new GetProjects();
		command.setViewOwnOrManage(true);
		command.setMappingMode(ProjectDTO.Mode._USE_PROJECT_MAPPER);

		view.getProjectsField().getStore().removeAll();

		dispatch.execute(command, new CommandResultHandler<ListResult<ProjectDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				if (Log.isErrorEnabled()) {
					Log.error("Error while retrieving projects list.", e);
				}
				N10N.error(I18N.CONSTANTS.createProjectTypeError(), I18N.CONSTANTS.createProjectTypeErrorDetails());
			}

			@Override
			public void onCommandSuccess(final ListResult<ProjectDTO> result) {

				final List<ProjectDTO> projects = result.getList();

				// Removes parent project itself.
				projects.remove(parentProject);

				// Checks if there is at least one available project.
				if (ClientUtils.isEmpty(projects)) {
					N10N.warn(I18N.CONSTANTS.createProjectTypeFundingSelectNone(), I18N.CONSTANTS.createProjectTypeFundingSelectNoneDetails());
					hideView();
					return;
				}

				// Sorts projects.
				Collections.sort(projects, PROJECT_NAME_COMPARATOR);

				// Generates a human-readable name to select a project and classify the projects by country.
				final Map<CountryDTO, List<ProjectDTO>> map = new TreeMap<CountryDTO, List<ProjectDTO>>(COUNTRY_NAME_COMPARATOR);

				for (final ProjectDTO project : projects) {

					final CountryDTO country = project.getCountry();
					project.generateTypeIconHTML(auth().getOrganizationId());

					if (map.containsKey(country)) {
						map.get(country).add(project);

					} else {
						final List<ProjectDTO> countryProjects = new ArrayList<ProjectDTO>();
						countryProjects.add(project);
						map.put(country, countryProjects);
					}
				}

				final List<ModelData> projectsListForCombo = new ArrayList<ModelData>();
				for (final CountryDTO country : map.keySet()) {

					projectsListForCombo.add(country);

					for (final ProjectDTO project : map.get(country)) {
						projectsListForCombo.add(project);
					}
				}

				view.getProjectsField().getStore().add(projectsListForCombo);
				view.getProjectsField().getStore().commitChanges();
			}
		}, view.getSaveButton(), view.getDeleteButton());
	}

	/**
	 * Method executed on save action event.
	 */
	private void onSaveAction() {

		if (!view.getForm().isValid()) {
			return;
		}

		// Retrieves the selected project and adds it as a new linked project (funding/funded).
		final ProjectDTO project = (ProjectDTO) view.getProjectsField().getSelection().get(0);

		// Sets the funding/funded parameters.
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(ProjectFundingDTO.PERCENTAGE, view.getAmountField().getValue().doubleValue());

		switch (projectType) {

			case FUNDING_PROJECT:
				parameters.put(ProjectFundingDTO.FUNDING_ID, project.getId());
				parameters.put(ProjectFundingDTO.FUNDED_ID, parentProject.getId());
				break;

			case FUNDED_PROJECT:
				parameters.put(ProjectFundingDTO.FUNDING_ID, parentProject.getId());
				parameters.put(ProjectFundingDTO.FUNDED_ID, project.getId());
				break;

			default:
				break;
		}

		// Creates the new funding/funded link.
		dispatch.execute(new CreateEntity(ProjectFundingDTO.ENTITY_NAME, parameters), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				if (Log.isErrorEnabled()) {
					Log.error("Error while creating a new linked project (funding/funded).", e);
				}
				N10N.warn(I18N.CONSTANTS.createProjectTypeFundingCreationError(), I18N.CONSTANTS.createProjectTypeFundingCreationDetails());
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				N10N.notification(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.createProjectTypeFundingSelectOk(), MessageType.INFO);

				final ProjectFundingDTO projectFunding = (ProjectFundingDTO) result.getEntity();

				// Notifies presenters displaying linked projects.
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.LINKED_PROJECT_UPDATE, projectType, projectFunding));

				hideView();
			}
		}, view.getSaveButton(), view.getDeleteButton());
	}

	/**
	 * Method executed on delete action event.
	 */
	private void onDeleteAction() {

		if (linkedProject == null) {
			throw new UnsupportedOperationException("Delete operation can only be processed with a provided linked project.");
		}

		N10N.confirmation(I18N.CONSTANTS.deleteConfirm(), I18N.CONSTANTS.deleteConfirmMessage(), new ConfirmCallback() {

			@Override
			public void onAction() {

				// Notifies project dashboard presenter of the remove action.
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.LINKED_PROJECT_DELETE, projectType, linkedProject));

				hideView();
			}
		});
	}

}
