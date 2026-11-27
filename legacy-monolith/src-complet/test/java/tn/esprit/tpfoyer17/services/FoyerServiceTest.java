package tn.esprit.tpfoyer17.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer17.entities.Foyer;
import tn.esprit.tpfoyer17.entities.Universite;
import tn.esprit.tpfoyer17.repositories.FoyerRepository;
import tn.esprit.tpfoyer17.repositories.UniversiteRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoyerServiceTest {

	@Mock
	private FoyerRepository foyerRepository;

	@Mock
	private UniversiteRepository universiteRepository;

	@InjectMocks
	private FoyerService foyerService;

	@Test
	void testGetAllFoyersAvecMockito() {
		List<Foyer> foyers = List.of(creerFoyer("F1", 100), creerFoyer("F2", 200));
		when(foyerRepository.findAll()).thenReturn(foyers);

		List<Foyer> resultat = foyerService.getAllFoyers();

		assertEquals(2, resultat.size());
		verify(foyerRepository).findAll();
	}

	@Test
	void testAjouterFoyerEtAffecterAUniversiteAvecMockito() {
		Foyer foyer = creerFoyer("Foyer affecte", 250);
		Universite universite = Universite.builder().nomUniversite("ESPRIT").build();
		when(foyerRepository.save(foyer)).thenReturn(foyer);
		when(universiteRepository.findById(1L)).thenReturn(Optional.of(universite));

		Foyer resultat = foyerService.ajouterFoyerEtAffecterAUniversite(foyer, 1L);

		assertEquals(foyer, resultat);
		assertEquals(foyer, universite.getFoyer());
		verify(universiteRepository).save(universite);
	}

	@Test
	void testAddFoyerAvecMockito() {
		Foyer foyer = creerFoyer("Foyer Mockito", 500);
		when(foyerRepository.save(foyer)).thenReturn(foyer);

		Foyer resultat = foyerService.addFoyer(foyer);

		assertNotNull(resultat);
		assertEquals("Foyer Mockito", resultat.getNomFoyer());
		verify(foyerRepository).save(foyer);
	}

	@Test
	void testGetFoyerByIdAvecMockito() {
		Foyer foyer = creerFoyer("Foyer recherche", 300);
		when(foyerRepository.findById(1L)).thenReturn(Optional.of(foyer));

		Foyer resultat = foyerService.getFoyerById(1L);

		assertNotNull(resultat);
		assertEquals("Foyer recherche", resultat.getNomFoyer());
		verify(foyerRepository).findById(1L);
	}

	@Test
	void testUpdateFoyerAvecMockito() {
		Foyer foyer = creerFoyer("Foyer modifie", 600);
		when(foyerRepository.save(foyer)).thenReturn(foyer);

		Foyer resultat = foyerService.updateFoyer(foyer);

		assertEquals("Foyer modifie", resultat.getNomFoyer());
		assertEquals(600, resultat.getCapaciteFoyer());
		verify(foyerRepository).save(foyer);
	}

	@Test
	void testDeleteFoyerAvecMockito() {
		doNothing().when(foyerRepository).deleteById(1L);

		foyerService.deleteFoyer(1L);

		verify(foyerRepository).deleteById(1L);
	}

	private Foyer creerFoyer(String nom, long capacite) {
		return Foyer.builder()
				.nomFoyer(nom)
				.capaciteFoyer(capacite)
				.build();
	}
}

@SpringBootTest
class FoyerServiceIntegrationTest {

	@Autowired
	private IFoyerService foyerService;

	@Test
	void testAddFoyerSansMockito() {
		Foyer foyer = creerFoyer("Foyer Integration Add", 400);

		Foyer resultat = foyerService.addFoyer(foyer);

		assertTrue(resultat.getIdFoyer() > 0);
		foyerService.deleteFoyer(resultat.getIdFoyer());
	}

	@Test
	void testGetFoyerByIdSansMockito() {
		Foyer foyer = foyerService.addFoyer(creerFoyer("Foyer Integration Get", 400));

		Foyer resultat = foyerService.getFoyerById(foyer.getIdFoyer());

		assertEquals(foyer.getIdFoyer(), resultat.getIdFoyer());
		foyerService.deleteFoyer(foyer.getIdFoyer());
	}

	@Test
	void testUpdateFoyerSansMockito() {
		Foyer foyer = foyerService.addFoyer(creerFoyer("Foyer avant modification", 400));
		foyer.setNomFoyer("Foyer apres modification");

		Foyer resultat = foyerService.updateFoyer(foyer);

		assertEquals("Foyer apres modification", resultat.getNomFoyer());
		foyerService.deleteFoyer(foyer.getIdFoyer());
	}

	@Test
	void testDeleteFoyerSansMockito() {
		Foyer foyer = foyerService.addFoyer(creerFoyer("Foyer Integration Delete", 400));

		long id = foyer.getIdFoyer();
		foyerService.deleteFoyer(id);

		org.junit.jupiter.api.Assertions.assertThrows(
				java.util.NoSuchElementException.class,
				() -> foyerService.getFoyerById(id)
		);
	}

	private Foyer creerFoyer(String nom, long capacite) {
		return Foyer.builder()
				.nomFoyer(nom)
				.capaciteFoyer(capacite)
				.build();
	}
}