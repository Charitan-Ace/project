// package ace.charitan.project.internal.project.service;

// import org.aspectj.lang.annotation.After;
// import org.aspectj.lang.annotation.Aspect;
// import org.aspectj.lang.annotation.Before;
// import org.springframework.stereotype.Component;

// import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;

// @Aspect
// @Component
// public class ShardAspect {

//     @Before("execution(* ace.charitan.project.internal.project.service.*.*(..)) && args(project,..)")
//     public void setShardContext(Project project) {
//         if (project.getStatusType() == StatusType.DELETED) {
//             ShardRoutingDataSource.setCurrentShard("PROJECT_DELETED");
//         } else {
//             ShardRoutingDataSource.setCurrentShard("PROJECT");
//         }
//     }

//     @After("execution(* ace.charitan.project.internal.project.service.*.*(..))")
//     public void clearShardContext() {
//         ShardRoutingDataSource.clearCurrentShard();
//     }
// }