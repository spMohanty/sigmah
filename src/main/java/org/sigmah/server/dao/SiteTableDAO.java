package org.sigmah.server.dao;

import java.util.List;

import org.sigmah.server.dao.util.SiteOrder;
import org.sigmah.server.dao.util.SiteProjectionBinder;
import org.sigmah.server.domain.User;
import org.sigmah.shared.util.Filter;

/**
 * Data Access Object for projections based on the {@link org.sigmah.server.domain.Site Site} domain object. Information
 * associated with Sites is stored across several entities, including {@link org.sigmah.server.domain.Location Location}
 * , {@link org.sigmah.server.domain.OrgUnit OrgUnit}, {@link org.sigmah.server.domain.AttributeValue AttributeValue},
 * {@link org.sigmah.server.domain.ReportingPeriod ReportingPeriod}, and {@link org.sigmah.server.domain.IndicatorValue
 * IndicatorValue} , but often we need this information in a table format with all the different data in columns, and
 * this class does the heavy lifting.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface SiteTableDAO {

	static final int RETRIEVE_ALL = 0xFF;
	static final int RETRIEVE_NONE = 0x00;
	static final int RETRIEVE_ADMIN = 0x01;
	static final int RETRIEVE_INDICATORS = 0x02;
	static final int RETRIEVE_ATTRIBS = 0x04;

	<RowT> List<RowT> query(User user, Filter filter, List<SiteOrder> orderings, SiteProjectionBinder<RowT> binder, int retrieve, int offset, int limit);

	int queryCount(User user, Filter filter);

	int queryPageNumber(User user, Filter filter, List<SiteOrder> orderings, int pageSize, int siteId);

}
