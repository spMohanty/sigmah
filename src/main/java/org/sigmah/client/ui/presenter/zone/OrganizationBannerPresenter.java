package org.sigmah.client.ui.presenter.zone;

import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractZonePresenter;
import org.sigmah.client.ui.res.ResourcesUtils;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.zone.OrganizationBannerView;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.servlet.ServletRequestBuilder;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.event.OfflineEvent;
import org.sigmah.client.event.handler.OfflineHandler;
import org.sigmah.offline.dao.LogoAsyncDAO;
import org.sigmah.offline.status.ApplicationState;

/**
 * Organization banner presenter displaying organization's name and logo.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class OrganizationBannerPresenter extends AbstractZonePresenter<OrganizationBannerPresenter.View> {

	/**
	 * View interface.
	 */
	@ImplementedBy(OrganizationBannerView.class)
	public static interface View extends ViewInterface {

		Panel getLogoPanel();

		Panel getNamePanel();

		HasHTML getNameLabel();

	}

	@Inject
	private LogoAsyncDAO logoAsyncDAO;
	
	private ApplicationState state;
	
	/**
	 * Default organization name.
	 */
	private static final String DEFAULT_ORGANIZATION_NAME = "Sigmah";

	/**
	 * Default organization logo URL.<br/>
	 * Should be a public resource file path.
	 */
	private static final String DEFAULT_ORGANIZATION_LOGO = ResourcesUtils.buildImageURL("header/org-default-logo.png");
	
	@Inject
	public OrganizationBannerPresenter(View view, Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Zone getZone() {
		return Zone.ORG_BANNER;
	}

	@Override
	public void onBind() {
		eventBus.addHandler(OfflineEvent.getType(), new OfflineHandler() {

			@Override
			public void handleEvent(OfflineEvent event) {
				state = event.getState();
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onZoneRequest(final ZoneRequest zoneRequest) {

		// Updates name widget.
		final String name = ClientUtils.isNotBlank(auth().getOrganizationName()) ? auth().getOrganizationName() : DEFAULT_ORGANIZATION_NAME;
		view.getNameLabel().setHTML(name);

		// Updates logo widget - requires a servlet request.
		if (isAnonymous()) {
			view.getLogoPanel().getElement().getStyle().setBackgroundImage(ResourcesUtils.buildCSSImageProperty(DEFAULT_ORGANIZATION_LOGO));
			return;
		}

		if(state != ApplicationState.OFFLINE) {
			final ServletRequestBuilder builder = new ServletRequestBuilder(injector, RequestBuilder.GET, Servlet.FILE, ServletMethod.DOWNLOAD_LOGO);
			builder.addParameter(RequestParameter.ID, auth().getOrganizationLogo());

			builder.send(new ServletRequestBuilder.RequestCallbackAdapter() {

				@Override
				public void onResponseReceived(final Request request, final Response response) {

					final String logoUrl;
					if (response.getStatusCode() == Response.SC_OK) {
						// Existing logo.
						logoUrl = response.getText();

						// Caching the organization logo.
						logoAsyncDAO.saveOrUpdate(auth().getOrganizationId(), logoUrl);

					} else {
						// Non existing logo.
						logoUrl = DEFAULT_ORGANIZATION_LOGO;
					}

					view.getLogoPanel().getElement().getStyle().setBackgroundImage(ResourcesUtils.buildCSSImageProperty(logoUrl));
				}
			});
			
		} else {
			logoAsyncDAO.get(auth().getOrganizationId(), new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) {
					view.getLogoPanel().getElement().getStyle().setBackgroundImage(ResourcesUtils.buildCSSImageProperty(DEFAULT_ORGANIZATION_LOGO));
				}

				@Override
				public void onSuccess(String result) {
					final String logoUrl;
					
					if(result != null) {
						logoUrl = result;
					} else {
						logoUrl = DEFAULT_ORGANIZATION_LOGO;
					}
					
					view.getLogoPanel().getElement().getStyle().setBackgroundImage(ResourcesUtils.buildCSSImageProperty(logoUrl));
				}
			});
		}
	}

}
