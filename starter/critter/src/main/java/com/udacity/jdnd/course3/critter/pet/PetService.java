package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.CustomerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public PetDTO getPetById(long petId) {
        Optional<Pet> optionalPet= petRepository.findById(petId);
        if(optionalPet.isPresent()){
            return convertEntityToPetDTO(optionalPet.get());
        }
        else {
            throw new NullPointerException();
        }
    }

    public List<PetDTO> getPets() {
        List<Pet> petList=petRepository.findAll();
        List<PetDTO> petDTOList= new ArrayList<>();
        for(Pet pet:petList){
            petDTOList.add(convertEntityToPetDTO(pet));
        }
        return petDTOList;
    }

    public List<PetDTO> getPetByOwnerId(long ownerId) {
        Optional<Customer> optionalCustomer=customerRepository.findById(ownerId);
        if(!optionalCustomer.isPresent()){
            throw new NullPointerException();
        }
        List<Pet> pets = petRepository.findAllPetByCustomerId(ownerId);
        List<PetDTO> petDTOs = new ArrayList<>();
        for(Pet pet : pets){
            petDTOs.add(convertEntityToPetDTO(pet));
        }
        return petDTOs;
    }
}
