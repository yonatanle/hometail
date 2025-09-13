package com.hometail.model;

/**
 * Represents the status of an adoption or service request in the HomeTail platform.
 * 
 * <p>This enum defines the possible states that a request can be in during its lifecycle.
 * The status helps track the progress of requests and determines what actions can be performed on them.</p>
 *
 * <p>The request statuses follow this typical flow:
 * <ol>
 *   <li>PENDING: Initial state when a request is first created</li>
 *   <li>APPROVED: Request has been accepted (may be a terminal state)</li>
 *   <li>REJECTED: Request has been declined (terminal state)</li>
 * </ol>
 * </p>
 *
 * @see com.hometail.model.AdoptionRequest
 * @since 1.0
 */
public enum RequestStatus {
    /**
     * The request has been created but no action has been taken yet.
     * This is the initial state for all new requests.
     */
    PENDING,

    /**
     * The request has been reviewed and approved by an administrator or shelter staff.
     * This may be a terminal state unless additional steps are required.
     */
    APPROVED,

    /**
     * The request has been reviewed and declined by an administrator or shelter staff.
     * This is a terminal state.
     */
    REJECTED,

}