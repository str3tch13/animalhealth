package com.techelevator.projects.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;
import com.techelevator.projects.model.jdbc.JDBCProjectDAO;

import junit.framework.Assert;

public class JDBCEmployeeDAOTest {

	private static final Long TEST_EMPLOYEE_ID = (long) 15;

	private static SingleConnectionDataSource dataSource;
	private JDBCEmployeeDAO dao;
	private JDBCProjectDAO daoProject;
	private JDBCDepartmentDAO daoDep;

	/* run once before any tests are run */
	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/Project_Organize_SQL");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");

// for the sake of time during class. should use env

		/*
		 * this line disables autocommit for any connections from datasource. This
		 * allows us to rollback any changes after each test
		 */
		dataSource.setAutoCommit(false);
	}

	@AfterClass
	public static void closeDataSource() {
		dataSource.destroy();
	}

	@Before

	public void setup() {
		String sqlInsertEmp = "TRUNCATE TABLE employee CASCADE; INSERT INTO employee"
				+ " (department_id, first_name, last_name, birth_date, gender, hire_date) \r\n"
				+ " VALUES (1, 'Brandon', 'Bob',  '2018-01-01', 'M', '2018-01-01'),"
				+ "(1, 'Dirk', 'Starwalker',  '2018-01-01', 'M', '2018-01-01') ;"
				+ " TRUNCATE TABLE project CASCADE; INSERT INTO project (name, from_date, to_date) "
				+ "VALUES ('test', '2018-01-01', '2018-01-01'); "

				+ " TRUNCATE TABLE project_employee CASCADE; INSERT INTO project_employee "
				+ "(project_id, employee_id) VALUES ((SELECT project_id FROM project WHERE name = 'test'),"
				+ " (SELECT employee_id FROM employee WHERE first_name = 'Brandon'));";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sqlInsertEmp);
		dao = new JDBCEmployeeDAO(dataSource);

	}

	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	@Test
	public void get_all_employees_test() {
		List<Employee> emp = dao.getAllEmployees();
		assertNotNull(emp);
		assertEquals(2, emp.size());
	}

	@Test
	public void search_employees_by_name_test() {
		List<Employee> emp = dao.searchEmployeesByName("Brandon", "Bob");
		assertNotNull(emp);
		assertEquals(1, emp.size());

	}

	@Test
	public void get_all_emp_by_dept_id_test() {
		List<Employee> emp = dao.getEmployeesByDepartmentId((long) 1);
		assertNotNull(emp);
		assertEquals(2, emp.size());
	}

	@Test
	public void get_all_employees_without_project_test() {
		String sqlInsertEmploString = "TRUNCATE TABLE project_employee CASCADE";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sqlInsertEmploString);
		dao = new JDBCEmployeeDAO(dataSource);
		List<Employee> empByDep = dao.getEmployeesWithoutProjects();
		assertEquals(2, empByDep.size());

	}

	@Test
	public void get_employees_by_project_id() {
		daoProject = new JDBCProjectDAO(dataSource);
		List<Project> projList = daoProject.getAllActiveProjects();
		Project proj = projList.get(0);
		Long projID = proj.getId();

		List<Employee> empList = dao.getEmployeesByProjectId(projID);
		Employee emp = empList.get(0);
		assertEquals("Brandon", emp.getFirstName());
	}

	@Test
	public void change_employees_dept() {
		List<Employee> empList = dao.getAllEmployees();
		Employee emp = empList.get(0);

		long emplID = emp.getId();
		long empDeptID = emp.getDepartmentId();

		dao.changeEmployeeDepartment(emplID, (long) 2);

		List<Employee> deptTwo = dao.getEmployeesByDepartmentId(2);
		List<Employee> deptOne = dao.getEmployeesByDepartmentId(1);

		assertEquals(deptOne.size(), deptTwo.size());

	}

}
