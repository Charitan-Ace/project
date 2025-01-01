package ace.charitan.project.internal.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import ace.charitan.project.internal.project.controller.ProjectRequestBody.SearchProjectsDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
class ProjectCustomRepository {
    @PersistenceContext
    private EntityManager entityManager;

    Page<InternalProjectDto> searchProjects(SearchProjectsDto searchProjectsDto, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Project> query = cb.createQuery(Project.class);

        Root<Project> project = query.from(Project.class);

        List<Predicate> predicates = new ArrayList<>();

        // Add condition for countryIsoCode if not null
        if (searchProjectsDto.getCountryIsoCode() != null) {
            predicates.add(cb.equal(project.get("countryIsoCode"), searchProjectsDto.getCountryIsoCode()));
        }

        // Add condition for name if not null
        if (searchProjectsDto.getName() != null) {
            predicates
                    .add(cb.like(cb.lower(project.get("name")), "%" + searchProjectsDto.getName().toLowerCase() + "%"));
        }

        // Add condition for startTime if not null
        if (searchProjectsDto.getStartTime() != null) {
            predicates.add(cb.greaterThanOrEqualTo(project.get("startTime"), searchProjectsDto.getStartTime()));
        }

        // Add condition for endTime if not null
        if (searchProjectsDto.getEndTime() != null) {
            predicates.add(cb.lessThanOrEqualTo(project.get("endTime"), searchProjectsDto.getEndTime()));
        }

        // Combine all predicates using AND
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Apply pagination by setting first result and max results
        TypedQuery<Project> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset()); // Set the offset for pagination
        typedQuery.setMaxResults(pageable.getPageSize()); // Set the page size

        // Get total count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Project> countRoot = countQuery.from(Project.class);
        countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[0])));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        // Create and return a Page object with the results and total count
        List<Project> results = typedQuery.getResultList();
        List<InternalProjectDto> internalProjectDtoList = results.stream()
                .map(project -> project.toInternalProjectDto()).collect(Collectors.toList());
        return new PageImpl<>(results, pageable, totalCount);
    }

}
