package sit.int202.kp2itbmshop.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int202.kp2itbmshop.dtos.AddUpdateBrandDto;
import sit.int202.kp2itbmshop.dtos.BrandDetailDto;
import sit.int202.kp2itbmshop.entities.Brand;
import sit.int202.kp2itbmshop.services.BrandService;
import sit.int202.kp2itbmshop.utils.ListMapper;

import java.util.List;

@RestController
@RequestMapping("/v1/brands")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class BrandController {
    @Autowired
    private BrandService brandService;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    public ResponseEntity<List<BrandDetailDto>> getAllBrands() {
        return ResponseEntity.ok(
                listMapper.mapList(brandService.getAllBrands(),BrandDetailDto.class,modelMapper)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDetailDto> getBrandById(@PathVariable Integer id) {
        Brand brand = brandService.getBrandById(id);
        BrandDetailDto brandDetailDto = brandService.getSaleItemCount(brand);
        return ResponseEntity.ok(brandDetailDto);
    }

    @PostMapping("")
    public ResponseEntity<BrandDetailDto> addBrand(
            @RequestBody AddUpdateBrandDto addUpdateBrandDto) {
        Brand brand = brandService.saveBrand(addUpdateBrandDto);
        BrandDetailDto response = modelMapper.map(brand, BrandDetailDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDetailDto> updateBrand(
            @PathVariable Integer id,
            @RequestBody AddUpdateBrandDto addUpdateBrandDto){
        addUpdateBrandDto.setId(id);
        Brand brand = brandService.updateBrand(addUpdateBrandDto);
        BrandDetailDto response = modelMapper.map(brand, BrandDetailDto.class);
        response.setNoOfSaleItems(brand.getSaleItems().size());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBrandById(@PathVariable Integer id) {
        brandService.deleteBrandById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
