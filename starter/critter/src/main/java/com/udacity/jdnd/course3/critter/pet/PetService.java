package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.CustomerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class PetService {
    @Autowired
    PetRepository petRepository;
    @Autowired
    CustomerRepository customerRepository;
    public PetDTO save(PetDTO petDTO) {
        Pet pet=convertPetDTOToEntity(petDTO);
        pet=petRepository.save(pet);
        Customer customer= pet.getCustomer();
        if(customer!=null){
            customer.addPet(pet);
            customerRepository.save(customer);
        }
        return  convertEntityToPetDTO(pet);
    }

    private PetDTO convertEntityToPetDTO(Pet pet) {
        PetDTO petDTO = new PetDTO();
        BeanUtils.copyProperties(pet, petDTO);
        if(pet.getCustomer() != null){
            petDTO.setOwnerId(pet.getCustomer().getId());
        }
        return petDTO;
    }

    private Pet convertPetDTOToEntity(PetDTO petDTO) {
        Pet pet = new Pet();
        BeanUtils.copyProperties(petDTO, pet);
        if(petDTO.getOwnerId() != 0){
            pet.setCustomer(customerRepository.findById(petDTO.getOwnerId()).get());
        }
        return pet;
    }
}
