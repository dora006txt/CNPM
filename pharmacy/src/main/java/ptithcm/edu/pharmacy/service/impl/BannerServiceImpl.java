package ptithcm.edu.pharmacy.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import ptithcm.edu.pharmacy.dto.BannerDTO;
import ptithcm.edu.pharmacy.dto.CreateBannerDTO;
import ptithcm.edu.pharmacy.dto.UpdateBannerDTO;
import ptithcm.edu.pharmacy.entity.Banner;
import ptithcm.edu.pharmacy.repository.BannerRepository;
import ptithcm.edu.pharmacy.service.BannerService;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    public BannerServiceImpl(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    private BannerDTO convertToDTO(Banner banner) {
        BannerDTO bannerDTO = new BannerDTO();
        BeanUtils.copyProperties(banner, bannerDTO);
        return bannerDTO;
    }

    @Override
    public List<BannerDTO> getAllBanners() {
        return bannerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BannerDTO getBannerById(Integer id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Banner not found with id: " + id));
        return convertToDTO(banner);
    }

    @Override
    public BannerDTO createBanner(CreateBannerDTO createBannerDTO) {
        Banner banner = new Banner();
        BeanUtils.copyProperties(createBannerDTO, banner);
        // Ensure default values from DTO are respected if not null
        if (createBannerDTO.getIsActive() != null) {
            banner.setActive(createBannerDTO.getIsActive());
        }
        if (createBannerDTO.getDisplayOrder() != null) {
            banner.setDisplayOrder(createBannerDTO.getDisplayOrder());
        }
        Banner savedBanner = bannerRepository.save(banner);
        return convertToDTO(savedBanner);
    }

    @Override
    public BannerDTO updateBanner(Integer id, UpdateBannerDTO updateBannerDTO) {
        Banner existingBanner = bannerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Banner not found with id: " + id));

        // Update only non-null fields from DTO
        if (updateBannerDTO.getName() != null)
            existingBanner.setName(updateBannerDTO.getName());
        if (updateBannerDTO.getImageUrl() != null)
            existingBanner.setImageUrl(updateBannerDTO.getImageUrl());
        if (updateBannerDTO.getTargetUrl() != null)
            existingBanner.setTargetUrl(updateBannerDTO.getTargetUrl());
        if (updateBannerDTO.getStartDate() != null)
            existingBanner.setStartDate(updateBannerDTO.getStartDate());
        if (updateBannerDTO.getEndDate() != null)
            existingBanner.setEndDate(updateBannerDTO.getEndDate());
        if (updateBannerDTO.getIsActive() != null)
            existingBanner.setActive(updateBannerDTO.getIsActive());
        if (updateBannerDTO.getDisplayOrder() != null)
            existingBanner.setDisplayOrder(updateBannerDTO.getDisplayOrder());

        Banner updatedBanner = bannerRepository.save(existingBanner);
        return convertToDTO(updatedBanner);
    }

    @Override
    public void deleteBanner(Integer id) {
        if (!bannerRepository.existsById(id)) {
            throw new EntityNotFoundException("Banner not found with id: " + id);
        }
        bannerRepository.deleteById(id);
    }

    @Override
    public List<BannerDTO> getActiveBanners() {
        return bannerRepository.findActiveBanners(LocalDateTime.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}