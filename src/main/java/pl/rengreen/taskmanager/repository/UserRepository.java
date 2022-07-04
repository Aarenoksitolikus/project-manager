package pl.rengreen.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.rengreen.taskmanager.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT DISTINCT * FROM user u WHERE u.user_id IN " +
            "(SELECT DISTINCT pu.user_id FROM project_user pu WHERE pu.project_id IN" +
            "(SELECT puz.project_id FROM project_user puz WHERE puz.user_id = ?))", nativeQuery = true)
    List<User> findAllByProjectsContains(Long userId);
    User findByEmail(String email);
}
