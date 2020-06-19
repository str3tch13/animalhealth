package com.techelevator.projects.view;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;

import junit.framework.Assert;

public class JDBCDepartmentDAOIntegrationTest {

	private static final long TEST_DEPARTMENT_ID = 100;

	/*
	 * use a single datasource so every database interaction is a part of the same
	 * session and transaction
	 */

	private static SingleConnectionDataSource dataSource;
	private JDBCDepartmentDAO dao;

	/* run once before any tests are run */
	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/Project_Organize_SQL");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");// for the sake of time during class. should use env

		/*
		 * this line disables autocommit for any connections from datasource. This
		 * allows us to rollback any changes after each test
		 */
		dataSource.setAutoCommit(false);
	}

	/* run once after all the tests are run */
	@AfterClass
	public static void closeDataSource() {
		dataSource.destroy();
	}

	/* befroe each test, let's insert a fake country and a fake city */
	@Before
	public void setup() {
		String sqlInsertDepartment = "INSERT INTO department (department_id, name) VALUES (?, 'Test Department')";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sqlInsertDepartment, TEST_DEPARTMENT_ID);
		dao = new JDBCDepartmentDAO(dataSource);
	}

	/*
	 * this will run after EACH test. We will rollback any changes we made for the
	 * setup or for the test
	 */
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	@Test
	public void get_all_departments_test() {
		List<Department> dept = dao.getAllDepartments();
		assertNotNull(dept);
		String sqlInsertDepartment = "SELECT * FROM department";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		SqlRowSet allDepartments = jdbcTemplate.queryForRowSet(sqlInsertDepartment);
		int departmentCounter = 0;
		while (allDepartments.next()) {
			departmentCounter++;
		}
		assertEquals(departmentCounter, dept.size());
	}

	@Test
	public void search_all_dept_by_name_test() {
		Department theDept = createDepartment("Stock");
		dao.createDepartment(theDept);

		List<Department> results = dao.searchDepartmentsByName("Stock");
		List<Department> checkedDepartments = new ArrayList<>();
		assertEquals(checkedDepartments, dao.searchDepartmentsByName("thereIsNoDepartmentThatHasThisStringInItsName"));

		assertNotNull(results);
		assertEquals(1, results.size());
		Assert.assertTrue(areDepartmentsEqual(theDept, results.get(0)));

	}

	@Test
	public void test_update_department() {
		Department d = createDepartment("A New Department");
		d = dao.createDepartment(d);
		// make sure it's there
		List<Department> deptFromDBList = dao.searchDepartmentsByName("A New Department");
		Department deptFromDB = deptFromDBList.get(0);
		// update it
		deptFromDB.setName("A Different Dept");
		dao.saveDepartment(deptFromDB);

		// make sure it's changed
		List<Department> upadatedDeptList = dao.searchDepartmentsByName("A Different Dept");
		Department upadatedDept = upadatedDeptList.get(0);
		assertEquals("A Different Dept", upadatedDept.getName());
		assertEquals(upadatedDept.getId(), d.getId());

	}

	@Test
	public void search_department_by_id_test() {

		Department dep = new Department();
		dep.setName("Bathroom");
		dep.setId(TEST_DEPARTMENT_ID);
		dao.createDepartment(dep);
		Department depRevived = dao.getDepartmentById(dep.getId());
		assertNotNull(depRevived);
		assertNotNull(dep);

		Department depDead = dao.getDepartmentById(null);
		assertNull(null, depDead);

	}

	@Test
	public void create_a_department_test() {

		Department theDept = new Department();
		theDept.setName("A different Dept");
		theDept.setId(TEST_DEPARTMENT_ID);
		dao.createDepartment(theDept);
		// YOU MUST READ FROM THE DATABASE AGAIN TO REALLY TEST
		Department createdDept = dao.getDepartmentById(theDept.getId());
		assertNotNull(theDept);
		assertNotNull(createdDept);
		Assert.assertTrue((areDepartmentsEqual(theDept, createdDept)));

	}

	private boolean areDepartmentsEqual(Department d1, Department d2) {

		// if 1 is null then they both have to be null
		if (d1 == null || d2 == null) {
			return d1 == null && d2 == null;
		}

		return d1.getId().equals(d2.getId()) && d1.getName().equals(d2.getName());
	}

//		
	private Department createDepartment(String name) {
		Department theDepartment = new Department();
		theDepartment.setName(name);
		theDepartment.setId(TEST_DEPARTMENT_ID);
		return theDepartment;
	}

}