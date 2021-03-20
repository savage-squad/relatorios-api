package br.com.desbravador.projetoacelera.users.service;

import br.com.desbravador.projetoacelera.auth.UserSecurity;
import br.com.desbravador.projetoacelera.auth.service.AuthService;
import br.com.desbravador.projetoacelera.users.domain.User;
import br.com.desbravador.projetoacelera.users.domain.repository.UserRepository;
import br.com.desbravador.projetoacelera.web.exception.AuthorizationException;
import br.com.desbravador.projetoacelera.web.exception.BusinessRuleException;
import br.com.desbravador.projetoacelera.web.exception.ResourceNotFoundException;
import br.com.desbravador.projetoacelera.web.service.DefaultService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UserService extends DefaultService<User, UserRepository>{

	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Override
	@Transactional
	public User save(User newAccount) {

		UserSecurity user = AuthService.authenticated();

		if (user == null || !user.hasRole("ROLE_ADMIN")) {
			throw new AuthorizationException("Access denied!");
		}
		
		super.repository.findByEmail(newAccount.getEmail()).ifPresent( function -> {
			throw new BusinessRuleException("E-mail already registered!"); 
		});

		newAccount.setPassword(RandomString.make(8));
		return repository.save(newAccount);
	}

	@Override
	@Transactional
	public User findOne(Long id) {

		UserSecurity user = AuthService.authenticated();

		if (user == null || !user.hasRole("ROLE_ADMIN") && !id.equals(user.getId())) {
			throw new AuthorizationException("Access denied!");
		}

		return repository
				.findById(id)
				.orElseThrow( () -> new ResourceNotFoundException("User not found!") );
	}

	@Transactional
	public User update(Long id, User inputUser) {

		User user = this.findOne(id);

		if (!user.getName().equals(inputUser.getName()) && inputUser.getName() != null) {
			user.setName(inputUser.getName());
		}

		if (user.isAdmin() != inputUser.isAdmin()) {
			user.setAdmin(inputUser.isAdmin());
		}

		user.setUpdatedAt(Instant.now());

		return super.repository.save(user);
	}

}
