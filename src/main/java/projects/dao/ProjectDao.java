package projects.dao;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase{
  private static final String CATEGORY_TABLE = "category";
  private static final String MATERIAL_TABLE = "material";
  private static final String PROJECT_TABLE = "project";
  private static final String PROJECT_CATEGORY_TABLE = "project_category";
  private static final String STEP_TABLE = "step";
  
  /**
   * connects to the database to add a new project, then returns the project with its auto-incremented project id
   * @param project
   * @return project
   * @throws Dbexception
   */
  public Project insertProject(Project project) {
    //@formatter:off
    String sql = "INSERT INTO " + PROJECT_TABLE 
        + " (project_name, estimated_hours, actual_hours, difficulty, notes)" 
        + " VALUES (?, ?, ?, ?, ? );";
    //@formatter:on
    try(Connection conn = DbConnection.getConnection()){
      startTransaction(conn);
      
      try(PreparedStatement stmt = conn.prepareStatement(sql)){
        setParameter(stmt, 1, project.getProjectName(), String.class);
        setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
        setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
        setParameter(stmt, 4, project.getDifficulty(), Integer.class);
        setParameter(stmt, 5, project.getNotes(), String.class);
        
        stmt.executeUpdate();
        Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
        commitTransaction(conn);
        project.setProjectId(projectId);
        return project;

      } catch(Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    } catch (SQLException e) {
      throw new DbException(e);
    }
  }

  /**
   * connects to the database to gather all project details
   * @return List of  all projects
   * @throws DbException
   */
  public List<Project> fetchAllProjects() {
    String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name;";
    try(Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);
      try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        try(ResultSet rs = stmt.executeQuery()) {
          List<Project> projects = new ArrayList<>();
          
          while(rs.next()) {
            projects.add(extract(rs, Project.class));
          }
          return projects;
        }
      } catch (Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    } catch (SQLException e) {
      throw new DbException(e);
    }
  }

  /**
   * connects to the database to fetch a given project
   * @param projectId
   * @return project with given ID
   * @throws DbException
   */
  public Optional<Project> fetchProjectById(Integer projectId) {
    String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?;";
    try(Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);
      try {
        Project project = null;
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
          setParameter(stmt, 1, projectId, Integer.class);
          try(ResultSet rs = stmt.executeQuery()){
            if (rs.next()) {
              project = extract(rs, Project.class);
            }
          }
        }
        if(Objects.nonNull(project)) {
          project.getMaterials().addAll(fetchProjectMaterials(conn, projectId));
          project.getSteps().addAll(fetchProjectSteps(conn, projectId));
          project.getCategories().addAll(fetchProjectCategories(conn, projectId));
        }
        commitTransaction(conn);
        return Optional.ofNullable(project);
      
      } catch (Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    } catch (SQLException e) {
      throw new DbException(e);
    }
  }

  /**
   * fetches the categories associated with a given project. uses an already open connection
   * @param conn
   * @param projectId
   * @return List of categories
   * @throws SQLException
   */
  private List<Category> fetchProjectCategories(Connection conn,
      Integer projectId) throws SQLException {
    
    // SELECT c.* FROM project_category pc JOIN category c USING (category_id) WHERE project_id = ?;
    String sql = "SELECT c.* FROM " + PROJECT_CATEGORY_TABLE + " pc "
        + "JOIN " + CATEGORY_TABLE + " c USING (category_id) "
        + "WHERE project_id = ?;";
    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameter(stmt, 1, projectId, Integer.class);
      try(ResultSet rs = stmt.executeQuery()) {
        List<Category> categories = new LinkedList<>();
        
        while(rs.next()) {
          categories.add(extract(rs, Category.class));
        }
        return categories;
      }
    }
  }

  /**
   * fetches all steps associated with a given project. uses an already open connection
   * @param conn
   * @param projectId
   * @return List of steps
   * @throws SQLException
   */
  private List<Step> fetchProjectSteps(Connection conn, Integer projectId) throws SQLException {
    String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ? ORDER BY step_id;";
    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, projectId, Integer.class);
        try(ResultSet rs = stmt.executeQuery()) {
          List<Step> steps = new LinkedList<>();
          
          while(rs.next()) {
            steps.add(extract(rs, Step.class));
          }
          return steps;
       }
    }
  }

  /**
   * fetches all materials associated with a given project. uses an already open connection
   * @param conn
   * @param projectId
   * @return List of materials
   * @throws SQLException
   */
  private List<Material> fetchProjectMaterials(Connection conn, Integer projectId) throws SQLException {
    String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameter(stmt, 1, projectId, Integer.class);
      try(ResultSet rs = stmt.executeQuery()) {
        List<Material> materials = new LinkedList<>();
        
        while(rs.next()) {
          materials.add(extract(rs, Material.class));
        }
        return materials;
      }
    }
  }

  /**
   * connects to the database to update the details of a project
   * @param updatedProject
   * @return the project with its data updated
   */
  public boolean modifyProjectDetails(Project updatedProject) {
    String sql = "UPDATE " + PROJECT_TABLE + " SET "
        + "project_name = ?, "
        + "estimated_hours = ?, "
        + "actual_hours = ?, "
        + "difficulty = ?, "
        + "notes = ? "
        + "WHERE project_id = ?;";
    try(Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);
      try(PreparedStatement stmt = conn.prepareStatement(sql)){
        setParameter(stmt, 1, updatedProject.getProjectName(), String.class);
        setParameter(stmt, 2, updatedProject.getEstimatedHours(), BigDecimal.class);
        setParameter(stmt, 3, updatedProject.getActualHours(), BigDecimal.class);
        setParameter(stmt, 4, updatedProject.getDifficulty(), Integer.class);
        setParameter(stmt, 5, updatedProject.getNotes(), String.class);
        setParameter(stmt, 6, updatedProject.getProjectId(), Integer.class);
        
        boolean successfulUpdate =  stmt.executeUpdate() == 1;
        commitTransaction(conn);
        return successfulUpdate;
        
      } catch (Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    } catch (SQLException e) {
      throw new DbException(e);
    }
  }
  /**
   * connects to the database to remove a project 
   * @param projectId
   * @return true if one row was removed from the project table, false otherwise
   */
  public boolean deleteProject(Integer projectId) {
    String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?;";
    try(Connection conn = DbConnection.getConnection()){
      startTransaction(conn);
      try(PreparedStatement stmt = conn.prepareStatement(sql)){
        setParameter(stmt, 1, projectId, Integer.class);
        
        boolean sucessfulDelete = stmt.executeUpdate() == 1;
        commitTransaction(conn);
        return sucessfulDelete;
        
      } catch (Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    } catch (SQLException e) {
      throw new DbException(e);
    }
  }
  /**
   * connects to the database to fetch the categories (name and ID)
   * @return List of categories
   */
  public List<Category> fetchAllCategories() {
    String sql = "SELECT * FROM " + CATEGORY_TABLE + " ORDER BY category_id;";
    try(Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);
      try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        try(ResultSet rs = stmt.executeQuery()) {
          List<Category> categories = new LinkedList<>();
          
          while(rs.next()) {
            categories.add(extract(rs, Category.class));
          }
          return categories;
        }
      } catch (Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
      
    } catch (SQLException e) {
      throw new DbException(e);
    }
  }
  /**
   * connects to the database to add a category to a project. 
   * Adds a new category to the category and project_category tables if necessary.
   * 
   * @param projectId
   * @param categoryName
   * @return true if one row of the project table is changed, otherwise false
   */
  public boolean addCategoryToProject(Integer projectId, String categoryName) {
    String sql = "INSERT INTO " + CATEGORY_TABLE + " (category_name) VALUES (?);";
    try(Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);
      try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, categoryName, String.class);
        
        boolean successfulUpdate =  stmt.executeUpdate() == 1;
        boolean anotherUpdate = false;
        if(successfulUpdate) {
          Integer categoryId = getCategoryId(conn, categoryName);
          anotherUpdate = upDateProjectCategoryTable(conn, projectId, categoryId);
        }
        if (anotherUpdate) {
          commitTransaction(conn);
          return successfulUpdate; 
        } else {
          throw new DbException();
        }
      } catch (Exception e) {
        rollbackTransaction(conn);
        throw new DbException(e);
      }
    } catch (SQLException e) {
      throw new DbException(e);
    }
  }
  /**
   * updates the project category table when a category is added to a project. Uses an already open connection
   * @param conn
   * @param projectId
   * @param categoryId
   * @return true if one row of the project_category table was updated, otherwise false
   * @throws SQLException
   */
  private boolean upDateProjectCategoryTable(Connection conn, Integer projectId, Integer categoryId) throws SQLException {
    String sql = "INSERT INTO " + PROJECT_CATEGORY_TABLE + " (project_id, category_id) VALUES (?, ?);";
    try(PreparedStatement stmt = conn.prepareStatement(sql)){
      setParameter(stmt, 1, projectId, Integer.class);
      setParameter(stmt, 2, categoryId, Integer.class);
      
      boolean successfulUpdate = stmt.executeUpdate() == 1;
      return successfulUpdate;
    }   
  }
  /**
   * gets the ID of a category for which the name is known. uses an already open connection.
   * @param conn
   * @param categoryName
   * @return  Integer categoryId
   * @throws SQLException
   */
  private Integer getCategoryId(Connection conn, String categoryName) throws SQLException {
    String sql = "SELECT * FROM " + CATEGORY_TABLE + " WHERE category_name = ?;";
    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameter(stmt, 1, categoryName, String.class);
      try(ResultSet rs = stmt.executeQuery()){
        Category id = new Category();
        
        while(rs.next()) {
          id = extract(rs, Category.class);
        }
        return id.getCategoryId();
      }
    } 
  }
  /**
   * adds a material to a project
   * @param Int projectId
   * @param Material material
   * @return true if one line of the material table was altered, otherwise false
   */
  public boolean addMaterialToProject(Integer projectId, Material material) {
    String sql = "INSERT INTO " + MATERIAL_TABLE
        + " (project_id, material_name, num_required, cost)"
        + " VALUES (?, ?, ?, ?);";
    
    try(Connection conn = DbConnection.getConnection()) {
      startTransaction(conn);
      try(PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameter(stmt, 1, material.getProjectId(), Integer.class);
        setParameter(stmt, 2, material.getMaterialName(), String.class);
        setParameter(stmt, 3, material.getNumRequired(), Integer.class);
        setParameter(stmt, 4, material.getCost(), BigDecimal.class);
        
        boolean successfulUpdate = (stmt.executeUpdate() == 1);
        if(successfulUpdate) {
          commitTransaction(conn);
        }
        return successfulUpdate;
      }
    } catch (SQLException e) {
      throw new DbException(e);
    }
  }
}
