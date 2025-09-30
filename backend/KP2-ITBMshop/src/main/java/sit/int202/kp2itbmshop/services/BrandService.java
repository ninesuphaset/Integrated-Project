package sit.int202.kp2itbmshop.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int202.kp2itbmshop.Exception.BadRequestException;
import sit.int202.kp2itbmshop.Exception.ItemNotFoundException;
import sit.int202.kp2itbmshop.dtos.AddUpdateBrandDto;
import sit.int202.kp2itbmshop.dtos.BrandDetailDto;
import sit.int202.kp2itbmshop.entities.Brand;
import sit.int202.kp2itbmshop.repositories.BrandRepository;
import sit.int202.kp2itbmshop.repositories.SaleItemRepository;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

//    public List<Brand> getAllBrandsIsActive() {
//        return brandRepository.findAllByIsActiveTrue();
//    }

    public Brand saveBrand(AddUpdateBrandDto addUpdateBrandDto) {
        addUpdateBrandDto.setId(null);
        if(brandRepository.existsByNameIgnoreCase(addUpdateBrandDto.getName())){
            throw new BadRequestException("Name already exists");
        }
        if(addUpdateBrandDto.getIsActive() == null) {
            addUpdateBrandDto.setIsActive(true);
        }
        Brand brand = modelMapper.map(addUpdateBrandDto, Brand.class);
        return brandRepository.save(brand);
    }

    public Brand updateBrand(AddUpdateBrandDto addUpdateBrandDto) {
        if(addUpdateBrandDto.getIsActive() == null) {
            addUpdateBrandDto.setIsActive(true);
        }
        Brand existing = brandRepository.findById(addUpdateBrandDto.getId())
                .orElseThrow(() -> new ItemNotFoundException(
                        "Brand not found with id: " + addUpdateBrandDto.getId()));
        modelMapper.map(addUpdateBrandDto, existing);
        return brandRepository.save(existing);
    }

    public Brand getBrandById(Integer id) {
        return brandRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Brand not found for this id :: "+id));
    }

    public BrandDetailDto getSaleItemCount(Brand brand){
        BrandDetailDto brandDetailDto = modelMapper.map(brand,BrandDetailDto.class);
        Integer count = saleItemRepository.countSaleitemByBrandId(brand.getId());
        brandDetailDto.setNoOfSaleItems(count);
        return brandDetailDto;
    }

    public void deleteBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(
                        "BrandId not found for this id : " + id));
       if (saleItemRepository.countSaleitemByBrandId(id) > 0) {
            throw new BadRequestException(
                    "Delete for BrandId : " + id + " is not allowed");
       }
        brandRepository.delete(brand);
    }
}
