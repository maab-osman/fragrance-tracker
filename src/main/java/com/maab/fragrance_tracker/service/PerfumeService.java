package com.maab.fragrance_tracker.service;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maab.fragrance_tracker.model.Perfume;
import com.maab.fragrance_tracker.model.User;
import com.maab.fragrance_tracker.repository.PerfumeRepository;

@Service
@Transactional(readOnly = true)
public class PerfumeService {

    private final PerfumeRepository perfumeRepository;

    public PerfumeService(PerfumeRepository perfumeRepository) {
        this.perfumeRepository = perfumeRepository;
    }

   public List<Perfume> findByUser(User user) {
    return perfumeRepository.findByUser(user);
}

    @Transactional
    public Perfume save(Perfume perfume) {
        return perfumeRepository.save(perfume);
    }

    public Optional<Perfume> findById(Long id) {
        return perfumeRepository.findById(id);
    }

    public List<Perfume> findByName(String name) {
        return perfumeRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Perfume> findByUserAndName(User user, String name) {
        List<Perfume> userPerfumes = perfumeRepository.findByUser(user);
        return userPerfumes.stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    @Transactional
    public void deleteById(Long id) {
        perfumeRepository.deleteById(id);
    }

    public List<Perfume> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }
}
