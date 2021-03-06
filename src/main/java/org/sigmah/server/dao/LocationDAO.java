package org.sigmah.server.dao;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Location;

/**
 * DAO for the {@link org.sigmah.server.domain.Location} domain object.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface LocationDAO extends DAO<Location, Integer> {

	/**
	 * Adds a link between the given {@link org.sigmah.server.domain.Location} and the given
	 * {@link org.sigmah.server.domain.AdminEntity AdminEntity}. If a link with another AdminEntity exists belonging to
	 * the same {@link org.sigmah.server.domain.AdminLevel AdminLevel}, it is removed.
	 */
	void updateAdminMembership(int locationId, int adminLevelId, int adminEntityId);

	/**
	 * Adds a link between the given {@link org.sigmah.server.domain.Location Location} and
	 * {@link org.sigmah.server.domain.AdminEntity AdminEntity}.
	 * 
	 * @param locationId
	 * @param adminEntityId
	 */
	void addAdminMembership(int locationId, int adminEntityId);

	/**
	 * Removes the link between the given {@link org.sigmah.server.domain.Location Location} and any
	 * {@link org.sigmah.server.domain.AdminEntity AdminEntity} belonging to the given
	 * {@link org.sigmah.server.domain.AdminLevel}.
	 * 
	 * @param locationId
	 * @param adminLevelId
	 */
	void removeMembership(int locationId, int adminLevelId);

}
