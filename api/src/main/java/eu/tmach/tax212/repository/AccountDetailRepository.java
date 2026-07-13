package eu.tmach.tax212.repository;

import eu.tmach.tax212.model.AccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailRepository extends JpaRepository<AccountDetail, String> {
}
