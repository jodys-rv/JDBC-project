package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import projects.dao.ProjectDao;
import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
  private ProjectDao projectDao = new ProjectDao();
  
  /**
   * adds a project
   * @param project
   * @return project
   */
  public Project addProject(Project project) {
    return projectDao.insertProject(project);
    
  }

  /**
   * fetches all the projects
   * @return List of projects
   */
  public List<Project> fetchAllProjects() {
    return projectDao.fetchAllProjects();
  }

  /**
   * fetches the project with a given project ID
   * @param projectId
   * @return project
   */
  public Project fetchProjectById(Integer projectId) {
    return projectDao.fetchProjectById(projectId).orElseThrow(
        () -> new NoSuchElementException("Project number " + projectId + " does not exist."));
  }

  /**
   * modifies the details of a project
   * @param updatedProject
   * @throws DbExeption
   */
  public void modifyProjectDetails(Project updatedProject) {
    if(!projectDao.modifyProjectDetails(updatedProject)) {
      throw new DbException("Project with ID = " + updatedProject.getProjectId() + " does not exist.");
    }
  }
  /**
   * deletes a project
   * @param projectId
   * @throws DbException
   */
  public void deleteProject(Integer projectId) {
    if (!projectDao.deleteProject(projectId)) {
      throw new DbException();
    }
  }
  /**
   * provides a list of the categories in the database
   * @return List of categories
   */
  public List<Category> fetchAllCategories() {
    return projectDao.fetchAllCategories();
  }
  /**
   * adds a category to a project
   * @param projectId
   * @param categoryName
   */
  public void addCategoryToProject(Integer projectId, String categoryName) {
    if(!projectDao.addCategoryToProject(projectId, categoryName)) {
      throw new DbException("Project with ID = " + projectId + " does not exist.");
    }
  }
  /**
   * adds a material to a project
   * @param projectId
   * @param material
   */
  public void addMaterialToProject(Integer projectId, Material material) {
    if(!projectDao.addMaterialToProject(projectId, material)) {
      throw new DbException();
    }
  }
}