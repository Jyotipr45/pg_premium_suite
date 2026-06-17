package com.jash.taskservice.config;

import com.jash.taskservice.domain.model.PropertyExpense;
import com.jash.taskservice.domain.model.RoomMaster;
import com.jash.taskservice.domain.model.UserMaster;
import com.jash.taskservice.repository.PropertyExpenseRepository;
import com.jash.taskservice.repository.RoomMasterRepository;
import com.jash.taskservice.repository.UserMasterRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MonthlyFinancialBillingJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(MonthlyFinancialBillingJob.class);

    private final RoomMasterRepository roomRepository;
    private final UserMasterRepository userRepository;
    private final PropertyExpenseRepository expenseRepository;

    public MonthlyFinancialBillingJob(
            RoomMasterRepository roomRepository,
            UserMasterRepository userRepository,
            PropertyExpenseRepository expenseRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("🧼 Monthly Financial Billing Job triggered at: {}", LocalDateTime.now());

        try {
            // 1. Process Tenant Rent Billings (Scan for occupied spaces)
            List<RoomMaster> occupiedRooms = roomRepository.findAll().stream()
                    .filter(RoomMaster::isOccupied)
                    .toList();

            for (RoomMaster room : occupiedRooms) {
                PropertyExpense rentInvoice = new PropertyExpense();
                rentInvoice.setPropertyId(room.getPropertyId());
                rentInvoice.setTitle("Automated Room Rent Invoice - Room " + room.getRoomNumber());
                rentInvoice.setAmount(room.getMonthlyRent());
                rentInvoice.setExpenseCategory("INCOME_EXPECTED");
                rentInvoice.setPaymentStatus("PENDING");
                rentInvoice.setDueDate(LocalDateTime.now().plusDays(5)); // Due by 5th of the month

                expenseRepository.save(rentInvoice);
            }
            logger.info("🧼 Successfully generated rent invariants for {} occupied room configurations.", occupiedRooms.size());

            // 2. Process Staff Payroll Billings (Scan for CARETAKER, CLEANER, COOK roles)
            List<UserMaster> staffMembers = userRepository.findAll().stream()
                    .filter(user -> "CARETAKER".equalsIgnoreCase(user.getUserRole()) 
                            || "CLEANER".equalsIgnoreCase(user.getUserRole()) 
                            || "COOK".equalsIgnoreCase(user.getUserRole()))
                    .toList();

            for (UserMaster staff : staffMembers) {
                if (staff.getAssociatedPropertyId() == null) continue;

                PropertyExpense salaryPayable = new PropertyExpense();
                salaryPayable.setPropertyId(staff.getAssociatedPropertyId());
                salaryPayable.setTitle("Monthly Payroll Allocation - " + staff.getFullName() + " (" + staff.getUserRole() + ")");
                
                // Assign a standard base salary allocation tier according to internal system roles
                double baseSalary = "CARETAKER".equalsIgnoreCase(staff.getUserRole()) ? 18000.0 : 12000.0;
                salaryPayable.setAmount(baseSalary);
                salaryPayable.setExpenseCategory("SALARY");
                salaryPayable.setPaymentStatus("PENDING");
                salaryPayable.setDueDate(LocalDateTime.now().plusDays(7)); // Disbursed by the 7th

                expenseRepository.save(salaryPayable);
            }
            logger.info("🧼 Successfully initialized payroll lines for {} active building operators.", staffMembers.size());

        } catch (Exception e) {
            logger.error("❌ Critical failure during automated monthly ledger iteration processing loops: {}", e.getMessage(), e);
            throw new JobExecutionException(e);
        }
    }
}