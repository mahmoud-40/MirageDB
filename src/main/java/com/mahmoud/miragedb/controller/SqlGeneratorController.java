package com.mahmoud.miragedb.controller;

import com.mahmoud.miragedb.dto.GenerateSqlRequest;
import com.mahmoud.miragedb.dto.GenerateSqlResponse;
import com.mahmoud.miragedb.service.SchemaGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mirage")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SqlGeneratorController {

    private final SchemaGenerationService schemaService;

    @PostMapping("/generate-sql")
    public ResponseEntity<GenerateSqlResponse> generateMockData(@RequestBody GenerateSqlRequest request) {

        String sql = schemaService.generateSqlFromEntities(request.entityClasses());

        return ResponseEntity.ok(new GenerateSqlResponse(sql));
    }
}