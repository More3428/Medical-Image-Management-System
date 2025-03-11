package com.example.MIMs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MIMs.entity.FileData;

public interface FileDataRepository extends JpaRepository<FileData, Integer>{
    Optional<FileData> findByName(String fileName);

}
