package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.EmployeeDAO;

public class JDBCEmployeeDAO implements EmployeeDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCEmployeeDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Employee> getAllEmployees() {
		List<Employee> allEmployees = new ArrayList<>();
		String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date FROM employee";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		while (results.next()) {
			Employee currentEmployee = mapRowToEmployee(results);
			allEmployees.add(currentEmployee);
		}
		return allEmployees;

	}

	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {
		List<Employee> foundEmployees = new ArrayList<>();
		String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date FROM employee WHERE first_name ILIKE ? AND last_name ILIKE ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, firstNameSearch, lastNameSearch);

		while (results.next()) {
			Employee currentEmployee = mapRowToEmployee(results);
			if (currentEmployee.getFirstName().equalsIgnoreCase(firstNameSearch)
					& currentEmployee.getLastName().equalsIgnoreCase(lastNameSearch)) {
				foundEmployees.add(currentEmployee);
			}

		}

		return foundEmployees;
	}

	@Override
	public List<Employee> getEmployeesByDepartmentId(long id) {
		List<Employee> departmentEmployees = new ArrayList<>();
		String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date FROM employee WHERE department_id = "
				+ id;
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		while (results.next()) {
			Employee currentEmployee = mapRowToEmployee(results);
			departmentEmployees.add(currentEmployee);
		}
		return departmentEmployees;
	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		List<Employee> noProjectEmployees = new ArrayList<>();
		String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date \r\n"
				+ "FROM employee \r\n" + "WHERE employee_id IN\r\n" + "(SELECT employee.employee_id \r\n"
				+ "FROM employee \r\n"
				+ "LEFT JOIN project_employee ON employee.employee_id = project_employee.employee_id \r\n"
				+ "WHERE project_employee.employee_id IS NULL)";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		while (results.next()) {
			Employee currentEmployee = mapRowToEmployee(results);
			noProjectEmployees.add(currentEmployee);
		}
		return noProjectEmployees;
	}

	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {
		List<Employee> projectEmployees = new ArrayList<>();
		String sql = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date FROM employee WHERE "
				+ "employee_id IN (SELECT employee.employee_id FROM employee JOIN project_employee ON employee.employee_id = project_employee.employee_id "
				+ "JOIN project ON project_employee.project_id = project.project_id " + "WHERE project.project_id = ?)";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, projectId);

		while (results.next()) {
			Employee currentEmployee = mapRowToEmployee(results);
			projectEmployees.add(currentEmployee);
		}
		return projectEmployees;
	}

	@Override
	public void changeEmployeeDepartment(Long employeeId, Long departmentId) {
		String sqlchangeEmployeeDepartment = "UPDATE employee\r\n" + "SET department_id = ?\r\n"
				+ "WHERE employee_id = ? ;";
		jdbcTemplate.update(sqlchangeEmployeeDepartment, departmentId, employeeId);
	}

	public Employee mapRowToEmployee(SqlRowSet results) {
		Employee mappedEmployee = new Employee();
		mappedEmployee.setId(results.getLong("employee_id"));
		mappedEmployee.setDepartmentId(results.getLong("department_id"));
		mappedEmployee.setFirstName(results.getString("first_name"));
		mappedEmployee.setLastName(results.getString("last_name"));
		mappedEmployee.setBirthDay(results.getDate("birth_date").toLocalDate());
		mappedEmployee.setGender(results.getString("gender").charAt(0));
		mappedEmployee.setHireDate(results.getDate("hire_date").toLocalDate());
		return mappedEmployee;
	}

}
