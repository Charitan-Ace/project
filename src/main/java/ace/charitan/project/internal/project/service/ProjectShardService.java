package ace.charitan.project.internal.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
class ProjectShardService {

    @Autowired
    @Qualifier("projectJdbcTemplate")
    private JdbcTemplate projectJdbcTemplate;

    @Autowired
    @Qualifier("projectDeletedJdbcTemplate")
    private JdbcTemplate projectDeletedJdbcTemplate;
}
