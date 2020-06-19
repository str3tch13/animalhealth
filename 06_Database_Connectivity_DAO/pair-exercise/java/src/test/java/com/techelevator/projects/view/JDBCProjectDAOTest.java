
package com.techelevator.projects.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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


import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;
import com.techelevator.projects.model.jdbc.JDBCProjectDAO;

public class JDBCProjectDAOTest {
private static final long TEST_PROJECT_ID = 5000;
private static SingleConnectionDataSource dataSource;
private JDBCProjectDAO dao;
private JDBCEmployeeDAO daoEmp;
private JDBCDepartmentDAO daoDept;

/* run once before any tests are run */
@BeforeClass
public static void setupDataSource() {
dataSource = new SingleConnectionDataSource();
dataSource.setUrl("jdbc:postgresql://localhost:5432/Project_Organize_SQL");
dataSource.setUsername("postgres");
dataSource.setPassword("postgres1");



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
String sqlInsertProject = "TRUNCATE TABLE project CASCADE; INSERT INTO project "
+ "(project_id, name, from_date, to_date) "
+ "VALUES (?, 'Majestic','1985-10-23','2020-01-23');"
+ " TRUNCATE TABLE employee CASCADE; INSERT INTO employee"
+ " (department_id, first_name, last_name, birth_date, gender, hire_date)"
+ " VALUES (1, 'Brandon', 'Bob',  '2018-01-01', 'M', '2018-01-01'),(1, 'Damen', 'Dark',  '2018-01-01', 'M', '2018-01-01');"

+ " TRUNCATE TABLE project_employee CASCADE; INSERT INTO project_employee (project_id, employee_id)\r\n" +
" VALUES ((SELECT project_id FROM project WHERE name = 'Majestic'), (SELECT employee_id FROM employee WHERE first_name = 'Josh'));";
   JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
   jdbcTemplate.update( sqlInsertProject, TEST_PROJECT_ID);
   dao = new JDBCProjectDAO(dataSource);
   daoEmp = new JDBCEmployeeDAO(dataSource);
   daoDept = new JDBCDepartmentDAO(dataSource);
}
@After
public void rollback() throws SQLException {
dataSource.getConnection().rollback();
}
@Test
public void get_all_active_project_test() {
List<Project> foundProject = dao.getAllActiveProjects();
assertNotNull(foundProject);
assertEquals(1, foundProject.size());


}
@Test
public void remove_emp_from_project_test() {

List<Employee> emp = daoEmp.getEmployeesByProjectId(TEST_PROJECT_ID);
Employee empRemove = emp.get(0);
Long empID = empRemove.getId();


dao.removeEmployeeFromProject(TEST_PROJECT_ID, empID);
List<Employee> emptyList = daoEmp.getEmployeesByProjectId(TEST_PROJECT_ID);
assertEquals(0, emptyList.size());

}


@Test
public void assign_emp_to_project_test() {

List<Employee> emp = daoEmp.getEmployeesWithoutProjects();
Employee empAdd = emp.get(0);
Long empID = empAdd.getId();


dao.addEmployeeToProject(TEST_PROJECT_ID, empID);

List<Employee> employeeOnTest = daoEmp.getEmployeesByProjectId(TEST_PROJECT_ID);
Employee empTest = employeeOnTest.get(1);
Long empTestId = empTest.getId();
assertEquals(empID, empTestId);
}

}