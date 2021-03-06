package org.sigmah.server.security.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.sigmah.client.page.Page;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.shared.command.AddOrgUnit;
import org.sigmah.shared.command.GetCategories;
import org.sigmah.shared.command.GetCountries;
import org.sigmah.shared.command.GetOrgUnit;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.SecureNavigationCommand;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Access rights configuration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
final class AccessRights {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AccessRights.class);

	/**
	 * Permissions map linking a secured token to a set of {@link GlobalPermissionEnum}.
	 */
	private static final Map<String, Pair<GrantType, Set<GlobalPermissionEnum>>> permissions = new HashMap<>();

	/**
	 * Unchecked resources tokens (they are always granted).
	 */
	private static final Set<String> grantedTokens = new HashSet<>();

	/**
	 * Token representing <em>missing tokens</em>.<br/>
	 * If a token is not declared among security permissions, this token is used.
	 */
	private static final String MISSING_TOKEN = "*";

	/**
	 * Permissions configuration.
	 */
	// TODO Complete permissions.
	static {

		// FIXME For the time being, all missing tokens are considered NOT secured. This line should be deleted in
		// production.
		sperm(MISSING_TOKEN, GrantType.BOTH);

		// Pages.
		sperm(pageToken(Page.LOGIN), GrantType.ANONYMOUS_ONLY);
		sperm(pageToken(Page.RESET_PASSWORD), GrantType.ANONYMOUS_ONLY);
		sperm(pageToken(Page.LOST_PASSWORD), GrantType.ANONYMOUS_ONLY);
		sperm(pageToken(Page.MOCKUP), GrantType.BOTH);
		sperm(pageToken(Page.CREDITS), GrantType.AUTHENTICATED_ONLY);
		sperm(pageToken(Page.HELP), GrantType.AUTHENTICATED_ONLY);

		sperm(pageToken(Page.DASHBOARD), GrantType.AUTHENTICATED_ONLY);

		sperm(pageToken(Page.PROJECT_DASHBOARD), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_PROJECT);
		sperm(pageToken(Page.PROJECT_DETAILS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_PROJECT);
		sperm(pageToken(Page.PROJECT_CALENDAR), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_PROJECT, GlobalPermissionEnum.VIEW_PROJECT_AGENDA);
		sperm(pageToken(Page.PROJECT_INDICATORS_ENTRIES), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_PROJECT, GlobalPermissionEnum.VIEW_INDICATOR);
		sperm(pageToken(Page.PROJECT_INDICATORS_MANAGEMENT), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_PROJECT, GlobalPermissionEnum.VIEW_INDICATOR);
		sperm(pageToken(Page.PROJECT_INDICATORS_MAP), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_PROJECT, GlobalPermissionEnum.VIEW_INDICATOR);
		sperm(pageToken(Page.PROJECT_LOGFRAME), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_PROJECT, GlobalPermissionEnum.VIEW_LOGFRAME);
		sperm(pageToken(Page.PROJECT_REPORTS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_PROJECT);

		sperm(pageToken(Page.INDICATOR_EDIT), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.MANAGE_INDICATOR);
		sperm(pageToken(Page.SITE_EDIT), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.MANAGE_INDICATOR);
		
		sperm(pageToken(Page.CREATE_PROJECT), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.CREATE_PROJECT);

		sperm(pageToken(Page.ORGUNIT_DASHBOARD), GrantType.AUTHENTICATED_ONLY);
		sperm(pageToken(Page.ORGUNIT_CALENDAR), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_PROJECT_AGENDA);
		sperm(pageToken(Page.ORGUNIT_DETAILS), GrantType.AUTHENTICATED_ONLY);
		sperm(pageToken(Page.ORGUNIT_REPORTS), GrantType.AUTHENTICATED_ONLY);

		sperm(pageToken(Page.ADMIN_PARAMETERS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN);
		sperm(pageToken(Page.ADMIN_CATEGORIES), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN);
		sperm(pageToken(Page.ADMIN_ORG_UNITS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN);
		sperm(pageToken(Page.ADMIN_USERS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN, GlobalPermissionEnum.MANAGE_USERS);
		sperm(pageToken(Page.ADMIN_PROJECTS_MODELS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN);
		sperm(pageToken(Page.ADMIN_REPORTS_MODELS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN);
		sperm(pageToken(Page.ADMIN_ORG_UNITS_MODELS), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN);
		sperm(pageToken(Page.ADMIN_IMPORTATION_SCHEME), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN);
		sperm(pageToken(Page.ADMIN_ADD_IMPORTATION_SCHEME), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN);
		sperm(pageToken(Page.ADMIN_ADD_VARIABLE_IMPORTATION_SCHEME), GrantType.AUTHENTICATED_ONLY, GlobalPermissionEnum.VIEW_ADMIN);

		// Commands.
		sperm(commandToken(AddOrgUnit.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(UpdateProject.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetCountries.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetValue.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetCategories.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetProjects.class), GrantType.AUTHENTICATED_ONLY);
		sperm(commandToken(GetOrgUnit.class), GrantType.AUTHENTICATED_ONLY);

		// Servlet methods.
		sperm(servletToken(Servlet.FILE, ServletMethod.DOWNLOAD_LOGO), GrantType.AUTHENTICATED_ONLY);
		sperm(servletToken(Servlet.FILE, ServletMethod.DOWNLOAD_FILE), GrantType.AUTHENTICATED_ONLY);
		sperm(servletToken(Servlet.FILE, ServletMethod.DOWNLOAD_ARCHIVE), GrantType.AUTHENTICATED_ONLY);
	}

	/**
	 * Granted tokens that are always granted in order to optimize application processes.
	 */
	static {
		grantedTokens.add(commandToken(SecureNavigationCommand.class));
	}

	/**
	 * Grants or refuse {@code user} access to the given {@code token}.
	 * 
	 * @param user
	 *          The user (authenticated or anonymous).
	 * @param token
	 *          The resource token (page, command, servlet method, etc.).
	 * @param originPageToken
	 *          The origin page token <em>(TODO Not used yet)</em>.
	 * @param mapper
	 *          The mapper service.
	 * @return {@code true} if the user is granted, {@code false} otherwise.
	 */
	static boolean isGranted(final User user, final String token, final String originPageToken, final Mapper mapper) {

		if (grantedTokens.contains(token)) {
			// Granted tokens ; avoids profile aggregation if user is authenticated.
			return true;
		}

		if (!permissions.containsKey(token)) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("No security permission can be found for token '{}'. Did you forget to declare corresponding 'sperm'?", token);
			}
			return isGranted(user, MISSING_TOKEN, originPageToken, mapper);
		}

		final Pair<GrantType, Set<GlobalPermissionEnum>> grantData = permissions.get(token);
		final GrantType grantType = grantData.left;

		final boolean granted;

		if (user == null || ServletExecutionContext.ANONYMOUS_USER.equals(user)) {
			// Anonymous user.
			granted = grantType != null && grantType != GrantType.AUTHENTICATED_ONLY;

		} else {
			// Authenticated user.
			if (grantType != null && grantType == GrantType.ANONYMOUS_ONLY) {
				granted = false;

			} else {
				final ProfileDTO aggregatedProfile = Handlers.aggregateProfiles(user, mapper);
				granted = CollectionUtils.containsAll(aggregatedProfile.getGlobalPermissions(), grantData.right);
			}
		}

		return granted;
	}

	// -------------------------------------------------------------------------------------
	//
	// TOKEN METHODS.
	//
	// -------------------------------------------------------------------------------------

	/**
	 * Return the <em>resource</em> token for the given servlet arguments.
	 * 
	 * @param servlet
	 *          The {@link Servlet} name.
	 * @param method
	 *          The {@link Servlet} method.
	 * @return the <em>resource</em> token for the given servlet arguments, or {@code null}.
	 */
	static String servletToken(final Servlet servlet, final ServletMethod method) {
		if (servlet == null || method == null) {
			return null;
		}
		return servlet.name() + '#' + method.name();
	}

	/**
	 * Return the <em>resource</em> token for the given {@code commandClass}.
	 * 
	 * @param commandClass
	 *          The {@link Command} class.
	 * @return the <em>resource</em> token for the given {@code commandClass}, or {@code null}.
	 */
	@SuppressWarnings("rawtypes")
	static String commandToken(final Class<? extends Command> commandClass) {
		if (commandClass == null) {
			return null;
		}
		return commandClass.getName();
	}

	/**
	 * Return the <em>resource</em> token for the given {@code page}.
	 * 
	 * @param page
	 *          The {@link Page} instance.
	 * @return the <em>resource</em> token for the given {@code page}, or {@code null}.
	 */
	static String pageToken(final Page page) {
		if (page == null) {
			return null;
		}
		return page.getToken();
	}

	// -------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------------

	private static enum GrantType {

		/**
		 * Access granted to <em>anonymous</em> user <b>only</b>.
		 */
		ANONYMOUS_ONLY,

		/**
		 * Access granted to <em>authenticated</em> users <b>only</b>.
		 */
		AUTHENTICATED_ONLY,

		/**
		 * Access granted to <em>anonymous</em> <b>and</b> <em>authenticated</em> users.
		 */
		BOTH;

	}

	/**
	 * <p>
	 * Registers a new <u>S</u>ecurity <u>PERM</u>ission for the given {@code token}.
	 * </p>
	 * <p>
	 * ;-)
	 * </p>
	 * 
	 * @param token
	 *          The resource token.
	 * @param grantType
	 *          The grant type, see {@link GrantType}.
	 * @param gpes
	 *          The {@link GlobalPermissionEnum} that the user needs to possess in order to be granted for the
	 *          {@code token}.
	 */
	private static void sperm(final String token, final GrantType grantType, final GlobalPermissionEnum... gpes) {
		permissions.put(token, new Pair<>(grantType, toSet(gpes)));
	}

	/**
	 * Transforms the given {@code gpes} array into a {@link Set}.<br/>
	 * Ignores {@code null} values in the process.
	 * 
	 * @param gpes
	 *          The {@link GlobalPermissionEnum} array.
	 * @return the given {@code gpes} array transformed into a {@link Set} with no {@code null} values.
	 */
	private static Set<GlobalPermissionEnum> toSet(final GlobalPermissionEnum... gpes) {

		final Set<GlobalPermissionEnum> set = new HashSet<GlobalPermissionEnum>();

		if (ArrayUtils.isEmpty(gpes)) {
			return set;
		}

		for (final GlobalPermissionEnum gpe : gpes) {
			if (gpe == null) {
				continue;
			}
			set.add(gpe);
		}

		return set;
	}

	/**
	 * Utility class constructor.
	 */
	private AccessRights() {
		// Only provides static constants.
	}

}
