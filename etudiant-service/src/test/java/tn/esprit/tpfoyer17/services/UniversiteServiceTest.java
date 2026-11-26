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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniversiteServiceTest {

	@Mock
	private UniversiteRepository universiteRepository;

	@Mock
	private FoyerRepository foyerRepository;

	@InjectMocks
	private UniversiteService universiteService;

	@Test
	void testAddUniversiteAvecMockito() {
		Universite universite = creerUniversite("ESPRIT", "Ariana");
		when(universiteRepository.save(universite)).thenReturn(universite);

		Universite resultat = universiteService.addUniversite(universite);

		assertNotNull(resultat);
		assertEquals("ESPRIT", resultat.getNomUniversite());
		verify(universiteRepository).save(universite);
	}

	@Test
	void testGetAllUniversitesAvecMockito() {
		when(universiteRepository.findAll()).thenReturn(List.of(creerUniversite("ESPRIT", "Ariana")));

		List<Universite> resultat = universiteService.getAllUniversites();

		assertEquals(1, resultat.size());
		verify(universiteRepository).findAll();
	}

	@Test
	void testGetUniversiteByIdAvecMockito() {
		Universite universite = creerUniversite("ESPRIT", "Ariana");
		when(universiteRepository.findById(1L)).thenReturn(Optional.of(universite));

		Universite resultat = universiteService.getUniversiteById(1L);

		assertNotNull(resultat);
		assertEquals("ESPRIT", resultat.getNomUniversite());
		verify(universiteRepository).findById(1L);
	}

	@Test
	void testUpdateUniversiteAvecMockito() {
		Universite universite = creerUniversite("ESPRIT Modifiee", "Tunis");
		when(universiteRepository.save(universite)).thenReturn(universite);

		Universite resultat = universiteService.updateUniversite(universite);

		assertEquals("ESPRIT Modifiee", resultat.getNomUniversite());
		assertEquals("Tunis", resultat.getAdresse());
		verify(universiteRepository).save(universite);
	}

	@Test
	void testDeleteUniversiteAvecMockito() {
		doNothing().when(universiteRepository).deleteById(1L);

		universiteService.deleteUniversite(1L);

		verify(universiteRepository).deleteById(1L);
	}

	@Test
	void testAffecterFoyerAUniversiteAvecMockito() {
		Universite universite = creerUniversite("ESPRIT", "Ariana");
		Foyer foyer = creerFoyer("Foyer El Amal");
		when(universiteRepository.findByNomUniversite("ESPRIT")).thenReturn(universite);
		when(foyerRepository.findById(1L)).thenReturn(Optional.of(foyer));
		when(universiteRepository.save(universite)).thenReturn(universite);

		Universite resultat = universiteService.affecterFoyerAUniversite(1L, "ESPRIT");

		assertNotNull(resultat.getFoyer());
		assertEquals("Foyer El Amal", resultat.getFoyer().getNomFoyer());
		verify(universiteRepository).save(universite);
	}

	@Test
	void testDesaffecterFoyerAUniversiteAvecMockito() {
		Universite universite = creerUniversite("ESPRIT", "Ariana");
		universite.setFoyer(creerFoyer("Foyer El Amal"));
		when(universiteRepository.findById(1L)).thenReturn(Optional.of(universite));
		when(universiteRepository.save(universite)).thenReturn(universite);

		Universite resultat = universiteService.desaffecterFoyerAUniversite(1L);

		assertNull(resultat.getFoyer());
		verify(universiteRepository).save(universite);
	}

	private Universite creerUniversite(String nom, String adresse) {
		return Universite.builder()
				.nomUniversite(nom)
				.adresse(adresse)
				.build();
	}

	private Foyer creerFoyer(String nom) {
		return Foyer.builder()
				.nomFoyer(nom)
				.capaciteFoyer(500)
				.build();
	}
}

@SpringBootTest
class UniversiteServiceIntegrationTest {

	@Autowired
	private IUniversiteService universiteService;

	@Test
	void testAddUniversiteSansMockito() {
		Universite universite = creerUniversite("ESPRIT Integration Add", "Ariana");

		Universite resultat = universiteService.addUniversite(universite);

		assertTrue(resultat.getIdUniversite() > 0);
		universiteService.deleteUniversite(resultat.getIdUniversite());
	}

	@Test
	void testGetUniversiteByIdSansMockito() {
		Universite universite = universiteService.addUniversite(
				creerUniversite("ESPRIT Integration Get", "Ariana"));

		Universite resultat = universiteService.getUniversiteById(universite.getIdUniversite());

		assertEquals(universite.getIdUniversite(), resultat.getIdUniversite());
		universiteService.deleteUniversite(universite.getIdUniversite());
	}

	@Test
	void testUpdateUniversiteSansMockito() {
		Universite universite = universiteService.addUniversite(
				creerUniversite("ESPRIT avant modification", "Ariana"));
		universite.setNomUniversite("ESPRIT apres modification");

		Universite resultat = universiteService.updateUniversite(universite);

		assertEquals("ESPRIT apres modification", resultat.getNomUniversite());
		universiteService.deleteUniversite(universite.getIdUniversite());
	}

	@Test
	void testDeleteUniversiteSansMockito() {
		Universite universite = universiteService.addUniversite(
				creerUniversite("ESPRIT Integration Delete", "Ariana"));

		long id = universite.getIdUniversite();
		universiteService.deleteUniversite(id);

		org.junit.jupiter.api.Assertions.assertThrows(
				java.util.NoSuchElementException.class,
				() -> universiteService.getUniversiteById(id)
		);
	}

	private Universite creerUniversite(String nom, String adresse) {
		return Universite.builder()
				.nomUniversite(nom)
				.adresse(adresse)
				.build();
	}
}