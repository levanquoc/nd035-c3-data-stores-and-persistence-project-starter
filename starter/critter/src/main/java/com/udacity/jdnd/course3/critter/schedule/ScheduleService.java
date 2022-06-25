package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.CustomerRepository;
import com.udacity.jdnd.course3.critter.user.Employee;
import com.udacity.jdnd.course3.critter.user.EmployeeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Transactional
@Service
public class ScheduleService {
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    PetRepository petRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    CustomerRepository customerRepository;
    public ScheduleDTO save(ScheduleDTO scheduleDTO) {
        Schedule schedule=convertScheduleDTOToEntity(scheduleDTO);
        schedule=scheduleRepository.save(schedule);
        return convertEntityToScheduleDTO(schedule);
    }

    private ScheduleDTO convertEntityToScheduleDTO(Schedule schedule) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(schedule, scheduleDTO);
        if(schedule.getPets() != null){
            List<Long> pets = new ArrayList<>();
            schedule.getPets().forEach(pet -> pets.add(pet.getId()));
            scheduleDTO.setPetIds(pets);
        }
        if(schedule.getEmployees() != null){
            List<Long> employees = new ArrayList<>();
            schedule.getEmployees().forEach(employee -> employees.add(employee.getId()));
            scheduleDTO.setEmployeeIds(employees);
        }
        return scheduleDTO;
    }

    private Schedule convertScheduleDTOToEntity(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);
        if(scheduleDTO.getPetIds() != null){
            Set<Pet> pets = new HashSet<>();
            scheduleDTO.getPetIds().forEach(petId -> pets.add(petRepository.findById(petId).get()));
            schedule.setPets(pets);
        }
        if(scheduleDTO.getEmployeeIds() != null){
            Set<Employee> employees = new HashSet<>();
            scheduleDTO.getEmployeeIds().forEach(employeeId -> employees.add(employeeRepository.findById(employeeId).get()));
            schedule.setEmployees(employees);
        }
        return schedule;
    }

    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> scheduleList=scheduleRepository.findAll();
        List<ScheduleDTO> scheduleDTOList=new ArrayList<>();
        for(Schedule schedule:scheduleList){
            scheduleDTOList.add(convertEntityToScheduleDTO(schedule));
        }
        return scheduleDTOList;
    }

    public List<ScheduleDTO> findScheduleByPetId(long petId) {
        Optional<Pet> petOptional= petRepository.findById(petId);
        if(petOptional.isPresent()){
            List<Schedule> schedules = scheduleRepository.findAllByPetsContaining(petOptional.get());
            List<ScheduleDTO> scheduleDTOs = new ArrayList<>();
            for(Schedule schedule : schedules) {
                scheduleDTOs.add(convertEntityToScheduleDTO(schedule));
            }
            return scheduleDTOs;
        }
        else {
            throw new NullPointerException();
        }
    }

    public List<ScheduleDTO> findScheduleByEmployeeId(long employeeId) {
        Optional<Employee> employeeOptional= employeeRepository.findById(employeeId);
        if(employeeOptional.isPresent()){
            List<Schedule> schedules = scheduleRepository.findAllByEmployeesContaining(employeeOptional.get());
            List<ScheduleDTO> scheduleDTOs = new ArrayList<>();
            for(Schedule schedule : schedules) {
                scheduleDTOs.add(convertEntityToScheduleDTO(schedule));
            }
            return scheduleDTOs;
        }
        else {
            throw new NullPointerException();
        }
    }

    public List<ScheduleDTO> getScheduleForCustomer(long customerId) {
        Optional<Customer> customerOptional=customerRepository.findById(customerId);
        if(customerOptional.isPresent()){
            List<Pet> petList= customerOptional.get().getPets();
            List<Schedule> scheduleList=scheduleRepository.findAllByPetsIn(petList);
            List<ScheduleDTO> scheduleDTOList=new ArrayList<>();
            for (Schedule schedule:scheduleList){
                scheduleDTOList.add(convertEntityToScheduleDTO(schedule));
            }
            return scheduleDTOList;
        }
        else throw new NullPointerException();

    }
}
