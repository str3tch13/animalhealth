package com.techelevator.projects.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;

public class JDBCEmployeeDAOTest {
	
	private static final Long TEST_EMPLOYEE_ID = (long) 123 ;
	
	private static SingleConnectionDataSource dataSource; 
	private JDBCEmployeeDAO dao;
	
	
	public JDBCEmployeeDAOTest() {
	
		
	}
		@BeforeClass
		public static void setupDataSource() {
			dataSource = new SingleConnectionDataSource();
			dataSource.setUrl("jdbc:postgresql://localhost:5432/Project_Organize_SQL");
			dataSource.setUsername("postgres");
			dataSource.setPassword("postgres1");//for the sake of time during class. should use env
			
			/* this line disables autocommit for any connections from datasource. This allows us to 
			 * rollback any changes after each test
			 */
			dataSource.setAutoCommit(false);
		
		
	
		}
		/* run once after all the tests are run */
		@AfterClass
		public static void closeDataSource() {
			dataSource.destroy();
		}
		
		@Before
		
		public void setup() {
			String sqlInsertEmploString = "TRUNCATE TABLE employee CASCADE; INSERT INTO employee "
					+ "( department_id, first_name, last_name, birth_date, gender, hire_date)" +
					"VALUES ( '1', 'Brandon', 'Bob',"
							+ "'1992-11-13','M','2020-11-07')"; 
		    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		    jdbcTemplate.update(sqlInsertEmploString);
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
		assertEquals(1, emp.size());
	}
		
		@Test
		public void search_employees_by_name_test() {
			List<Employee> emp = dao.searchEmployeesByName("Brandon", "Bob");
			assertNotNull(emp);
			assertEquals(1, emp.size());
			
			}
		@Test
		public void get_all_employee_by_dept_id_test () {
			List<Employee> empByDep = dao.getEmployeesByDepartmentId((long) 1);
			assertNotNull(empByDep);
			assertEquals(1, empByDep.size());
			
		}
		@Test
		public void get_all_employees_without_project_test () {
			String sqlInsertEmploString = "TRUNCATE TABLE project_employee CASCADE";
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		    jdbcTemplate.update(sqlInsertEmploString);
		    dao = new JDBCEmployeeDAO(dataSource);
			List<Employee> empByDep = dao.getEmployeesWithoutProjects();
			assertEquals(1, empByDep.size());
			
}
		@Test
		public void get_all_employees_with_project_test () {
			String sqlInsertEmploString = "TRUNCATE TABLE project_employee CASCADE; INSERT INTO project ( "
					+ "name, from_date, to_date) VALUES ('test_project','1992-11-13','2020-11-07');";
			String sqlInsertEmploString2 = " INSERT INTO project_employee (project_id, employee_id)" +
							" VALUES ((SELECT project_id FROM project WHERE name = 'test_project'), (SELECT employee_id FROM employee "
							+ " WHERE first_name = 'Brandon'))";
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		    jdbcTemplate.update(sqlInsertEmploString);
		    jdbcTemplate.update(sqlInsertEmploString2);
		    dao = new JDBCEmployeeDAO(dataSource);
		    String sqlFindProjectId = "SELECT project_id FROM project WHERE name = 'test_project';";
		    Long project_id_location = (long) jdbcTemplate.update(sqlFindProjectId);
			List<Employee> empByDep = dao.getEmployeesByProjectId(project_id_location);
			assertEquals(1, empByDep.size());
	}
		
}

