package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;

public class JDBCProjectDAO implements ProjectDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCProjectDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Project> getAllActiveProjects() {
		List<Project> FoundProject = new ArrayList<Project>();
		String sqlFoundProject = "SELECT project_id, name, from_date,to_date\r\n" + "FROM project\r\n"
				+ "WHERE to_date IS NOT NULL and from_date IS NOT NULL; ";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFoundProject);

		while (results.next()) {
			Project listedProjects = mapRowToProject(results);
			FoundProject.add(listedProjects);
		}

		return FoundProject;
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String sqlRemoveEmployee = "DELETE  \r\n" + "FROM project_employee\r\n"
				+ "WHERE employee_id = ? AND project_id = ?;";
		jdbcTemplate.update(sqlRemoveEmployee, employeeId, projectId);
	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String sqlAddEmployee = "INSERT INTO project_employee(project_id, employee_id) \r\n" + "VALUES (?,?); ";
		jdbcTemplate.update(sqlAddEmployee, projectId, employeeId);
	}

	private Project mapRowToProject(SqlRowSet results) {
		Project foundProject = new Project();
		foundProject.setId(results.getLong("project_id"));
		foundProject.setName(results.getString("name"));
		foundProject.setStartDate(results.getDate("from_date").toLocalDate());
		foundProject.setEndDate(results.getDate("to_date").toLocalDate());
		return foundProject;
	}

}

//package com.techelevator.projects.model.jdbc;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.sql.DataSource;
//
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import com.techelevator.projects.model.Project;
//import com.techelevator.projects.model.ProjectDAO;
//
//public class JDBCProjectDAO implements ProjectDAO {
//
//	private JdbcTemplate jdbcTemplate;
//
//	public JDBCProjectDAO(DataSource dataSource) {
//		this.jdbcTemplate = new JdbcTemplate(dataSource);
//	}
//	
//	@Override
//	public List<Project> getAllActiveProjects() {
//		return new ArrayList<>();
//	}
//
//	@Override
//	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
//		
//	}
//
//	@Override
//	public void addEmployeeToProject(Long projectId, Long employeeId) {
//		
//	}
//
//}
