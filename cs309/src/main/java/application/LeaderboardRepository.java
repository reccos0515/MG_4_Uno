package application;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This interface manages the repository to interact with the Leaderboard table.
 * Extends the JpaRepository.
 * @author Dakota Moore
 */
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long>{

}
