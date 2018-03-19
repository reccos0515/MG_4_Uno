package secondexperiment.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import secondexperiment.domain.Player;

public interface PlayerRepo extends JpaRepository<Player, String> {

}
