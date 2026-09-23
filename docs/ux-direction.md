# RxLocate pharmacy portal UX direction

## Product direction

The pharmacy portal is being positioned as a **calm, exception-first operations command center**. It should feel closer to a premium enterprise resource planning product than a conventional CRUD admin screen. The interface must make the next operational decision obvious: what is at risk, what changed, what needs replenishment, and what can be safely published to patients.

The redesign reduces explanatory copy and increases information hierarchy. The default dashboard leads with a restrained KPI row, an action centre, recent activity, and compact trend visuals. Inventory, sales, and reports use the same visual language so the portal reads as one coherent product.

## High-value portal features

The next product-quality features should be built around traceability and safe action rather than visual decoration. Medicine records should eventually support lot number, expiration and removal dates, supplier, storage location, available quantity, reserved quantity, and inventory state. Relevant states include available, low stock, out of stock, quarantined, recalled, expired, and pending inspection.

The inventory roadmap should include FEFO guidance, an expiry worklist, receiving validation, reorder-point exceptions, projected stockout date, days of supply, transfer suggestions, cycle counts, and a recall workspace. Quantity-changing actions should preserve an audit trail with actor, timestamp, before/after quantity, lot, location, and reason.

## Visual and interaction principles

The portal uses warm mineral surfaces, graphite type, one restrained teal operational accent, green and amber risk cues, hairline borders, rounded but controlled containers, and shallow shadows. Charts are compact and actionable rather than ornamental. Bar segments and KPI cards should drill into the underlying inventory or sales records. Every chart should expose a timeframe, unit, freshness state, and next action.

Risk is never encoded by color alone. Every status uses a text label, and the visual system reserves saturated color for operational attention. Tables remain semantic and focused; secondary detail should move into record views or expandable details rather than creating extremely wide grids.

## Research references

- [Odoo expiration dates](https://www.odoo.com/documentation/19.0/applications/inventory_and_mrp/inventory/product_management/product_tracking/expiration_dates.html)
- [Odoo FEFO removal strategy](https://www.odoo.com/documentation/19.0/applications/inventory_and_mrp/inventory/shipping_receiving/removal_strategies/fefo.html)
- [NetSuite inventory management](https://www.netsuite.com/portal/products/erp/warehouse-fulfillment/inventory-management.shtml)
- [ASHP drug recall management guidance](https://www.ashp.org/-/media/assets/pharmacy-practice/resource-centers/patient-safety/doc/Guidance-on-Drug-Recall-Management.pdf)
- [FDA drug recalls](https://www.fda.gov/drugs/drug-recalls/understanding-drug-recalls-what-know-and-what-do)
- [Stripe dashboards](https://docs.stripe.com/dashboard/basics)
- [Linear dashboards](https://linear.app/docs/dashboards)
- [Vercel Analytics](https://vercel.com/docs/analytics)
- [GOV.UK table pattern](https://design-system.service.gov.uk/components/table/)
- [WCAG status messages](https://www.w3.org/WAI/WCAG22/Understanding/status-messages.html)

## Implementation note

The first pass intentionally upgrades the existing server-rendered portal without inventing new database fields. It uses the current dashboard snapshot, inventory table, sales activity, and report aggregates so the visual layer is safe to deploy. Lot-level traceability, recalls, replenishment intelligence, and audit events are documented as the next architecture phase rather than simulated in the UI.
