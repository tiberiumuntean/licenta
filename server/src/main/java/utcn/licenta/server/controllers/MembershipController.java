package utcn.licenta.server.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utcn.licenta.server.models.DTO.MembershipDTO;
import utcn.licenta.server.models.Membership;
import utcn.licenta.server.models.stripe.CheckoutProduct;
import utcn.licenta.server.models.stripe.ClientSecret;
import utcn.licenta.server.services.MembershipService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/memberships")
public class MembershipController {
    @Autowired
    MembershipService membershipService;

    @GetMapping("/{id}")
    public MembershipDTO getOne(@PathVariable int id) {
        return membershipService.getOne(id);
    }

    @GetMapping
    public List<MembershipDTO> getAll() {
        return membershipService.getAll();
    }

    @PostMapping
    public MembershipDTO create(@RequestBody Membership membership) {
        return membershipService.create(membership);
    }

    @PutMapping("/{id}")
    public MembershipDTO update(@PathVariable int id, @RequestBody Membership membership) {
        return membershipService.update(id, membership);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        membershipService.delete(id);
    }

    @PostMapping("/create-payment-intent")
    public ClientSecret paymentWithCheckoutPage(@RequestBody CheckoutProduct checkoutProduct) throws StripeException {
        Stripe.apiKey = "sk_test_51L5SIKGu5a3fHJbN2xCW8Db6WXGnfjQI6NuDHpa7iWGz3mCICFrFvm4RvnfLYa3shOlilQUIGhorYV8GaGNFOT9B00eio9I8u2";

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder().setAmount(checkoutProduct.getPrice() * 100).setCurrency("ron").build();

        PaymentIntent intent = PaymentIntent.create(params);
        String clientSecret = intent.getClientSecret();

        return new ClientSecret(clientSecret);
    }
}
