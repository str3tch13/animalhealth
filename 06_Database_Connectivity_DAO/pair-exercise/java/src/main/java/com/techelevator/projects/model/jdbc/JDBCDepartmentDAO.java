package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.DepartmentDAO;

public class JDBCDepartmentDAO implements DepartmentDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCDepartmentDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Department> getAllDepartments() {
		List<Department> allDepartments = new ArrayList<>();
		String sql = "SELECT department_id, name FROM department";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		while (results.next()) {
			Department currentDepartment = mapRowToDepartment(results);
			allDepartments.add(currentDepartment);
		}
		return allDepartments;

	}

	@Override
	public List<Department> searchDepartmentsByName(String nameSearch) {
		List<Department> foundDepartments = new ArrayList<>();
		String sql = "SELECT department_id, name FROM department WHERE name ILIKE ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, "%" + nameSearch + "%");

		while (results.next()) {
			Department currentDepartment = mapRowToDepartment(results);
			
//				if (currentDepartment.getName().equalsIgnoreCase(nameSearch)) {
					foundDepartments.add(currentDepartment);
//				}
			
			

		}

		return foundDepartments;
	}

	@Override
	public void saveDepartment(Department updatedDepartment) {
		String sql = "UPDATE department SET name = ? WHERE department_id = ?";
		jdbcTemplate.update(sql, updatedDepartment.getName(), updatedDepartment.getId());
	}

	@Override
	public Department createDepartment(Department newDepartment) {
		
//		String sql = "INSERT INTO department (name, department_id) " +
//				"VALUES (?, ?)";
//		jdbcTemplate.update(sql,newDepartment.getName(), newDepartment.getId());			
//		return newDepartment; 
		
		
		String sql = "INSERT INTO department (name) " +
		"VALUES (?) RETURNING department_id";
		SqlRowSet result = jdbcTemplate.queryForRowSet(sql,newDepartment.getName());
		while (result.next()) {
			newDepartment.setId(result.getLong("department_id")); 
			} 			
		return newDepartment; 
		
		
//		String sql = "INSERT INTO department (name) VALUES (?" + ")";
//		jdbcTemplate.update(sql, newDepartment.getName());
//		
//		return newDepartment;
	}

	@Override
	public Department getDepartmentById(Long id) {
		String sql = "SELECT name FROM department WHERE department_id = " + id;
		SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
		Department theSearchedDepartment = mapRowToDepartment(result);
		return theSearchedDepartment;
	}

	public Department mapRowToDepartment(SqlRowSet results) {
		Department mappedDepartment = new Department();
		mappedDepartment.setId(results.getLong("department_id"));
		mappedDepartment.setName(results.getString("name"));
		return mappedDepartment;
	}

}
