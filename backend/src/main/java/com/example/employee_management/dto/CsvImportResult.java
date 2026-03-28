package com.example.employee_management.dto;

public class CsvImportResult {
    private int imported;
    private int skipped;

    public CsvImportResult() {
    }

    public CsvImportResult(int imported, int skipped) {
        this.imported = imported;
        this.skipped = skipped;
    }

    public int getImported() {
        return imported;
    }

    public void setImported(int imported) {
        this.imported = imported;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }
}
