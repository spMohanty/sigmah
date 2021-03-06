package org.sigmah.client.inject;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.page.PageManager;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.ui.presenter.ApplicationPresenter;
import org.sigmah.client.ui.presenter.CreateProjectPresenter;
import org.sigmah.client.ui.presenter.CreditsPresenter;
import org.sigmah.client.ui.presenter.DashboardPresenter;
import org.sigmah.client.ui.presenter.HelpPresenter;
import org.sigmah.client.ui.presenter.LoginPresenter;
import org.sigmah.client.ui.presenter.MockUpPresenter;
import org.sigmah.client.ui.presenter.admin.AdminPresenter;
import org.sigmah.client.ui.presenter.admin.CategoriesAdminPresenter;
import org.sigmah.client.ui.presenter.admin.ParametersAdminPresenter;
import org.sigmah.client.ui.presenter.admin.ReportModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.importation.AddImportationSchemePresenter;
import org.sigmah.client.ui.presenter.admin.importation.AddVariableImporationSchemePresenter;
import org.sigmah.client.ui.presenter.admin.importation.ImportationSchemeAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.AddBudgetSubFieldPresenter;
import org.sigmah.client.ui.presenter.admin.models.EditFlexibleElementAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.EditLayoutGroupAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.importer.AddImportationSchemeModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.importer.AddMatchingRuleImportationShemeModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.importer.ImportModelPresenter;
import org.sigmah.client.ui.presenter.admin.models.orgunit.AddOrgUnitModelAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.orgunit.OrgUnitModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.project.AddProjectModelAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.project.EditPhaseModelAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.project.ProjectModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.orgunits.AddOrgUnitAdminPresenter;
import org.sigmah.client.ui.presenter.admin.orgunits.MoveOrgUnitAdminPresenter;
import org.sigmah.client.ui.presenter.admin.orgunits.OrgUnitsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.users.PrivacyGroupEditPresenter;
import org.sigmah.client.ui.presenter.admin.users.ProfileEditPresenter;
import org.sigmah.client.ui.presenter.admin.users.UserEditPresenter;
import org.sigmah.client.ui.presenter.admin.users.UsersAdminPresenter;
import org.sigmah.client.ui.presenter.calendar.CalendarEventPresenter;
import org.sigmah.client.ui.presenter.importation.ImportationPresenter;
import org.sigmah.client.ui.presenter.orgunit.OrgUnitCalendarPresenter;
import org.sigmah.client.ui.presenter.orgunit.OrgUnitDashboardPresenter;
import org.sigmah.client.ui.presenter.orgunit.OrgUnitDetailsPresenter;
import org.sigmah.client.ui.presenter.orgunit.OrgUnitPresenter;
import org.sigmah.client.ui.presenter.orgunit.OrgUnitReportsPresenter;
import org.sigmah.client.ui.presenter.password.LostPasswordPresenter;
import org.sigmah.client.ui.presenter.password.ResetPasswordPresenter;
import org.sigmah.client.ui.presenter.project.LinkedProjectPresenter;
import org.sigmah.client.ui.presenter.project.ProjectCalendarPresenter;
import org.sigmah.client.ui.presenter.project.ProjectDetailsPresenter;
import org.sigmah.client.ui.presenter.project.ProjectPresenter;
import org.sigmah.client.ui.presenter.project.ProjectReportsPresenter;
import org.sigmah.client.ui.presenter.project.dashboard.ProjectDashboardPresenter;
import org.sigmah.client.ui.presenter.project.export.ExportProjectsPresenter;
import org.sigmah.client.ui.presenter.project.export.ExportProjectsSettingPresenter;
import org.sigmah.client.ui.presenter.project.indicator.EditIndicatorPresenter;
import org.sigmah.client.ui.presenter.project.indicator.EditSitePresenter;
import org.sigmah.client.ui.presenter.project.indicator.ProjectIndicatorEntriesPresenter;
import org.sigmah.client.ui.presenter.project.indicator.ProjectIndicatorManagementPresenter;
import org.sigmah.client.ui.presenter.project.indicator.ProjectIndicatorMapPresenter;
import org.sigmah.client.ui.presenter.project.logframe.ProjectLogFramePresenter;
import org.sigmah.client.ui.presenter.project.projectcore.ProjectCoreDiffPresenter;
import org.sigmah.client.ui.presenter.project.projectcore.ProjectCoreRenameVersionPresenter;
import org.sigmah.client.ui.presenter.reminder.ReminderEditPresenter;
import org.sigmah.client.ui.presenter.reminder.ReminderHistoryPresenter;
import org.sigmah.client.ui.presenter.reports.AttachFilePresenter;
import org.sigmah.client.ui.presenter.reports.ReportCreatePresenter;
import org.sigmah.client.ui.presenter.zone.AppLoaderPresenter;
import org.sigmah.client.ui.presenter.zone.AuthenticationBannerPresenter;
import org.sigmah.client.ui.presenter.zone.MenuBannerPresenter;
import org.sigmah.client.ui.presenter.zone.MessageBannerPresenter;
import org.sigmah.client.ui.presenter.zone.OfflineBannerPresenter;
import org.sigmah.client.ui.presenter.zone.OrganizationBannerPresenter;
import org.sigmah.client.ui.theme.Theme;
import org.sigmah.offline.dao.FileDataAsyncDAO;
import org.sigmah.offline.dao.TransfertAsyncDAO;
import org.sigmah.offline.dispatch.LocalDispatchServiceAsync;
import org.sigmah.offline.inject.OfflineModule;
import org.sigmah.offline.status.ConnectionStatus;
import org.sigmah.offline.sync.Synchronizer;
import org.sigmah.shared.file.TransfertManager;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import org.sigmah.offline.status.ApplicationStateManager;

/**
 * GIN injector.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@GinModules(value = {
											ClientModule.class,
											OfflineModule.class
})
public interface Injector extends Ginjector {

	EventBus getEventBus();

	DispatchAsync getDispatch();

	LocalDispatchServiceAsync getLocalDispatch();

	PageManager getPageManager();

	Theme getTheme();

	AuthenticationProvider getAuthenticationProvider();

	ApplicationStateManager getApplicationStateManager();

	TransfertManager getTransfertManager();

	Synchronizer getSynchronizer();

	UserLocalCache getClientCache();

	// --------------------------------------------------
	//
	// Asynchronous DAO (offline mode).
	//
	// --------------------------------------------------

	FileDataAsyncDAO getFileDataAsyncDAO();

	TransfertAsyncDAO getTransfertAsyncDAO();

	// --------------------------------------------------
	//
	// Presenters.
	//
	// --------------------------------------------------

	ApplicationPresenter getApplicationPresenter();

	// ---- Zones presenters.

	MockUpPresenter getMockUpPresenter();

	OrganizationBannerPresenter getOrganizationBannerPresenter();

	AuthenticationBannerPresenter getAuthenticationBannerPresenter();

	OfflineBannerPresenter getOfflineBannerPresenter();

	AppLoaderPresenter getAppLoaderPresenter();

	MenuBannerPresenter getMenuBannerPresenter();

	MessageBannerPresenter getMessageBannerPresenter();

	CreditsPresenter getCreditsPresenter();

	HelpPresenter getHelpPresenter();

	// ---- Pages presenters.

	DashboardPresenter getHomePresenter();

	LoginPresenter getLoginPresenter();

	LostPasswordPresenter getLostPasswordPresenter();

	ResetPasswordPresenter getResetPasswordPresenter();

	CreateProjectPresenter getCreateProjectPresenter();

	CalendarEventPresenter getCalendarEventPresenter();

	ReportCreatePresenter getReportCreatePresenter();

	AttachFilePresenter getAttachFilePresenter();

	ImportationPresenter getImportationPresenter();

	// ---- Project presenters.

	ProjectPresenter getProjectPresenter();

	ProjectDashboardPresenter getProjectDashboardPresenter();

	ProjectLogFramePresenter getProjectLogFramePresenter();

	ProjectDetailsPresenter getProjectDetailsPresenter();

	ProjectCalendarPresenter getProjectCalendarPresenter();

	ProjectReportsPresenter getProjectReportsPresenter();

	ReminderEditPresenter getReminderEditPresenter();

	ReminderHistoryPresenter getReminderHistoryPresenter();

	LinkedProjectPresenter getLinkedProjectPresenter();

	ProjectIndicatorEntriesPresenter getProjectIndicatorEntriesPresenter();

	ProjectIndicatorManagementPresenter getProjectIndicatorManagementPresenter();

	ProjectIndicatorMapPresenter getProjectIndicatorMapPresenter();

	EditIndicatorPresenter getEditIndicatorPresenter();

	EditSitePresenter getEditSitePresenter();

	ExportProjectsPresenter getExportProjectsPresenter();

	ExportProjectsSettingPresenter getExportProjectsSettingPresenter();

	ProjectCoreRenameVersionPresenter getProjectCoreRenameVersionPresenter();

	ProjectCoreDiffPresenter getProjectCoreDiffPresenter();

	// ---- OrgUnit presenters.

	OrgUnitPresenter getOrgUnitPresenter();

	OrgUnitDashboardPresenter getOrgUnitDashboradPresenter();

	OrgUnitDetailsPresenter getOrgUnitDetailsPresenter();

	OrgUnitCalendarPresenter getOrgUnitCalendarPresenter();

	OrgUnitReportsPresenter getOrgUnitReportsPresenter();

	// ---- Admin presenters

	AdminPresenter getAdminPresenter();

	ParametersAdminPresenter getParametersAdminPresenter();

	UsersAdminPresenter getUsersAdminPresenter();

	PrivacyGroupEditPresenter getPrivacyGroupEditPresenter();

	ProfileEditPresenter getProfileEditPresenter();

	UserEditPresenter getUserEditPresenter();

	OrgUnitsAdminPresenter getOrgUnitsAdminPresenter();

	AddOrgUnitAdminPresenter getAddOrgUnitAdminPresenter();

	MoveOrgUnitAdminPresenter getMoveOrgUnitAdminPresenter();

	CategoriesAdminPresenter getCategoriesAdminPresenter();

	OrgUnitModelsAdminPresenter getOrgUnitModelsAdminPresenter();

	AddOrgUnitModelAdminPresenter getAddOrgUnitModelAdminPresenter();

	ProjectModelsAdminPresenter getProjectModelsAdminPresenter();

	AddProjectModelAdminPresenter getAddProjectModelAdminPresenter();

	AddImportationSchemePresenter getAddImportationSchemePresenter();

	ReportModelsAdminPresenter getReportModelsAdminPresenter();

	EditPhaseModelAdminPresenter getEditPhaseModelAdminPresenter();

	EditLayoutGroupAdminPresenter getEditLayoutGroupAdminPresenter();

	EditFlexibleElementAdminPresenter getEditFlexibleElementAdminPresenter();

	AddBudgetSubFieldPresenter getAddBudgetSubFieldPresenter();

	ImportModelPresenter getImportModelPresenter();

	ImportationSchemeAdminPresenter getImportationShemePresenter();

	AddVariableImporationSchemePresenter getAddVariableImporationSchemePresenter();

	AddImportationSchemeModelsAdminPresenter getAddImportationSchemeModelsAdminPresenter();

	AddMatchingRuleImportationShemeModelsAdminPresenter getAddMatchingRuleImportationShemeModelsAdminPresenter();
}
